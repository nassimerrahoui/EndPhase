package app.components;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import app.CVM;
import app.interfaces.appareil.IAjoutAppareil;
import app.interfaces.appareil.IConsommation;
import app.interfaces.appareil.ILaveLinge;
import app.interfaces.assembleur.IComposantDynamique;
import app.ports.lavelinge.LaveLingeAssembleurInPort;
import app.ports.lavelinge.LaveLingeCompteurOutPort;
import app.ports.lavelinge.LaveLingeControleurOutPort;
import app.ports.lavelinge.LaveLingeInPort;
import app.util.ModeLaveLinge;
import app.util.TemperatureLaveLinge;
import app.util.TypeAppareil;
import app.util.URI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.ports.PortI;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import fr.sorbonne_u.utils.PlotterDescription;
import simulator.models.lavelinge.LaveLingeCoupledModel;
import simulator.models.lavelinge.LaveLingeModel;
import simulator.plugins.LaveLingeSimulatorPlugin;

@OfferedInterfaces(offered = { ILaveLinge.class, IComposantDynamique.class })
@RequiredInterfaces(required = { IAjoutAppareil.class, IConsommation.class })
public class LaveLinge 
	extends AbstractCyPhyComponent 
	implements EmbeddingComponentAccessI {

	/** port sortant permettant a l'appareil de s'inscrire sur la liste des apparails du controleur */
	protected LaveLingeControleurOutPort controleur_OUTPORT;
	
	/** port sortant permettant au compteur de recupere la consommation du lave-linge */
	protected LaveLingeCompteurOutPort consommation_OUTPORT;

	/** Gestion de priorite pour les decisions du controleur*/
	protected TypeAppareil type;
	
	/** Etat actuel de l'appareil */
	protected ModeLaveLinge etat;
	
	protected ArrayList<ModeLaveLinge> planification_etats;
	
	protected int heure;
	protected int minutes;
	
	/** Consommation en Watts par l'appareil */
	protected Double consommation;
	protected TemperatureLaveLinge temperature;
	
	protected LaveLingeSimulatorPlugin asp;
	
	public static int ORIGIN_X = CVM.plotX;
	public static int ORIGIN_Y = CVM.plotY;

	protected LaveLinge(
			String LAVELIGNE_URI, 
			String LAVELINGE_COMPTEUR_OP_URI,
			String LAVELINGE_CONTROLEUR_OP_URI,
			int nbThreads, int nbSchedulableThreads, 
			TypeAppareil type) throws Exception {
		super(LAVELIGNE_URI, nbThreads, nbSchedulableThreads);

		controleur_OUTPORT = new LaveLingeControleurOutPort(LAVELINGE_CONTROLEUR_OP_URI,this);
		consommation_OUTPORT = new LaveLingeCompteurOutPort(LAVELINGE_COMPTEUR_OP_URI,this);
		
		// port entrant permettant au controleur d'effectuer des actions sur le lave-linge
		LaveLingeInPort action_INPORT = new LaveLingeInPort(this);
		
		// port entrant permettant a l'assembleur d'effectuer d'integrer l'entite au logement
		LaveLingeAssembleurInPort launch_INPORT = new LaveLingeAssembleurInPort(this);
		
		controleur_OUTPORT.publishPort();
		consommation_OUTPORT.publishPort();
		action_INPORT.publishPort();
		launch_INPORT.publishPort();
		
		if (AbstractCVM.isDistributed) {
			this.executionLog.setDirectory(System.getProperty("user.dir")) ;
		} else {
			this.executionLog.setDirectory(System.getProperty("user.home")) ;
		}
		
		this.createNewExecutorService(URI.POOL_ACTION_LAVELINGE_URI.getURI(), 5, false) ;
		
		// affichage
		this.tracer.setTitle("LaveLinge");
		this.tracer.setRelativePosition(1, 1);
		
		// attributs
		this.heure = 0;
		this.minutes = 0;
		this.etat = ModeLaveLinge.OFF;
		this.planification_etats = new ArrayList<ModeLaveLinge>();
		this.consommation = 0.0;
		this.type = type;
		this.temperature = TemperatureLaveLinge.QUARANTE_DEGRES;
		
		this.initialise();
	}
	
	/**
	 * Ajoute l'URI de l'appareil a la map des appareils du controleur
	 * @param uri
	 * @throws Exception
	 */
	public void demandeAjoutControleur(String uri) throws Exception {
		this.controleur_OUTPORT.demandeAjoutControleur(uri, this.getClass().getName(), this.type);
	}

	/**
	 * Envoie la consommation au compteur
	 * @param uri
	 * @param consommation
	 * @throws Exception
	 */
	public void envoyerConsommation(String uri, double consommation) throws Exception {
		this.consommation_OUTPORT.envoyerConsommation(uri, consommation);
	}

	/**
	 * Modifie l'etat du lave-linge
	 * @param etat
	 * @throws Exception
	 */
	public void setModeLaveLinge(ModeLaveLinge etat) throws Exception {
		if(etat == ModeLaveLinge.OFF || etat == ModeLaveLinge.VEILLE)
			this.etat = etat;
	}

	/**
	 * Definit une planification qui est une suite d'etats 
	 * que le lave-linge doit prendre dans le futur
	 * @param planification
	 * @param heure
	 * @param min
	 * @throws Exception
	 */
	public void planifierCycle(ArrayList<ModeLaveLinge> planification, int heure, int min) throws Exception {
		if(heure >= 0 && heure <= 23 && min >= 0 && min <= 59) {
			this.planification_etats = planification;
			this.heure = heure;
			this.minutes = min;
			this.logMessage("Cycle planifier a : " + heure + "h" + min);
		}
	}

	/**
	 * Modifie la temperature de l'eau du lave-linge
	 * @param tl
	 */
	public void setTemperature(TemperatureLaveLinge tl) {
		this.temperature = tl;
	}
	
	/**
	 * Gerer et afficher ce qui se passe pendant un mode
	 */
	public void runningAndPrint() {
		this.logMessage("Mode actuel : " + etat.name());
		this.logMessage("...");
	}
	
	// ************* Cycle de vie du composant ************* 

	@Override
	public void start() throws ComponentStartException {
		super.start();
		this.logMessage("Demarrage du lave-linge...");
	}
	
	/**
	 * Execution depuis l'assembleur
	 * @throws Exception
	 */
	public void dynamicExecute() throws Exception {
		
		this.logMessage("Phase d'execution du lave-linge.");
		
		this.logMessage("Execution en cours...");
		
		this.scheduleTaskWithFixedDelay(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try { ((LaveLinge) this.getTaskOwner()).runningAndPrint(); } 
				catch (Exception e) { throw new RuntimeException(e); }
			}
		}, 2000, 5000, TimeUnit.MILLISECONDS);
		
		this.scheduleTaskWithFixedDelay(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try { ((LaveLinge) this.getTaskOwner()).envoyerConsommation(URI.LAVELINGE_URI.getURI(), consommation); } 
				catch (Exception e) { throw new RuntimeException(e); }
			}
		}, 2500, 1000, TimeUnit.MILLISECONDS);
		
		execute();
	}
	
	@Override
	public void execute() throws Exception {
		SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 10L ;

		HashMap<String,Object> simParams = new HashMap<String,Object>() ;
		simParams.put(LaveLingeModel.URI + " : " + LaveLingeModel.COMPONENT_REF, this);
		
		simParams.put(LaveLingeModel.URI + " : " + LaveLingeModel.POWER_PLOTTING_PARAM_NAME, new PlotterDescription(
				"Consommation Lave-Linge", 
				"Temps (sec)", 
				"Consommation (Watt)", 
				ORIGIN_X ,
		  		ORIGIN_Y,
		  		getPlotterWidth(),
		  		getPlotterHeight())) ;
		
		this.asp.setSimulationRunParameters(simParams) ;

		this.runTask(
				new AbstractComponent.AbstractTask() {
					@Override
					public void run() {
						try {
							asp.doStandAloneSimulation(0.0, 60000.0) ;
						} catch (Exception e) {
							throw new RuntimeException(e) ;
						}
					}
				});
		
		Thread.sleep(10L);
		
		this.scheduleTaskWithFixedDelay(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try {
					((LaveLinge) this.getTaskOwner()).etat = (ModeLaveLinge) ((LaveLinge) this.getTaskOwner()).asp.getModelStateValue(LaveLingeModel.URI, "state");
					((LaveLinge) this.getTaskOwner()).consommation = (Double) ((LaveLinge) this.getTaskOwner()).asp.getModelStateValue(LaveLingeModel.URI, "consommation");
					((LaveLinge) this.getTaskOwner()).logMessage("Mode : " + etat);
					((LaveLinge) this.getTaskOwner()).logMessage("Consommation : " + consommation);
				} catch (Exception e) { e.printStackTrace(); }
			}
		}, 2500, 1000, TimeUnit.MILLISECONDS);
	}
	
	@Override
	public void finalise() throws Exception {
		this.logMessage("Arret du composant lave-linge...") ;
		super.finalise();
	}
	
	@Override
	public void	shutdown() throws ComponentShutdownException
	{
		try {
			PortI[] port_controleur = this.findPortsFromInterface(ILaveLinge.class);
			PortI[] port_consommation = this.findPortsFromInterface(IConsommation.class);
			PortI[] port_ajoutappareil = this.findPortsFromInterface(IAjoutAppareil.class);
			PortI[] port_assembleur = this.findPortsFromInterface(IComposantDynamique.class);
			
			port_controleur[0].unpublishPort() ;
			port_consommation[0].unpublishPort();
			port_ajoutappareil[0].unpublishPort();
			port_assembleur[0].unpublishPort();
		} catch (Exception e) { throw new ComponentShutdownException(e); }
		super.shutdown();
	}

	@Override
	public void shutdownNow() throws ComponentShutdownException
	{
		try {
			PortI[] port_controleur = this.findPortsFromInterface(ILaveLinge.class);
			PortI[] port_consommation = this.findPortsFromInterface(IConsommation.class);
			PortI[] port_ajoutappareil = this.findPortsFromInterface(IAjoutAppareil.class);
			PortI[] port_assembleur = this.findPortsFromInterface(IComposantDynamique.class);
			
			port_controleur[0].unpublishPort() ;
			port_consommation[0].unpublishPort();
			port_ajoutappareil[0].unpublishPort();
			port_assembleur[0].unpublishPort();
		} catch (Exception e) { throw new ComponentShutdownException(e); }
		super.shutdownNow();
	}
	
	// ******************* Simulation *************************

	@Override
	protected Architecture createLocalArchitecture(String architectureURI) throws Exception {
		return LaveLingeCoupledModel.build();
	}

	@Override
	public Object getEmbeddingComponentStateValue(String name) throws Exception {
		if(name.equals(LaveLingeModel.URI + " : state")) {
			return etat;
		} else if(name.equals(LaveLingeModel.URI + " : consommation")) {
			return consommation; 
		} else if(name.equals(LaveLingeModel.URI + " : delai") ) {
			return (double) heure * 3600 + minutes * 60;
		} else if(name.equals(LaveLingeModel.URI + " : temperature")) {
			return temperature;
		} else if(name.equals(LaveLingeModel.URI + " : planification")) {
			ArrayList<ModeLaveLinge> temp = planification_etats;
			planification_etats = null;
			return temp;
		}
		return null;
	}
	
	/**
	 * Installe le plugin
	 * @throws Exception
	 */
	protected void initialise() throws Exception {
		
		Architecture localArchitecture = this.createLocalArchitecture(null) ;
		this.asp = new LaveLingeSimulatorPlugin();
		this.asp.setPluginURI(localArchitecture.getRootModelURI());
		this.asp.setSimulationArchitecture(localArchitecture);
		this.installPlugin(this.asp);
	}
	
	// ************** Plotter ******************************
	
	public static int getPlotterWidth() {
		int ret = Integer.MAX_VALUE ;
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment() ;
		GraphicsDevice[] gs = ge.getScreenDevices() ;
		for (int i = 0; i < gs.length; i++) {
			DisplayMode dm = gs[i].getDisplayMode() ;
			int width = dm.getWidth() ;
			if (width < ret) {
				ret = width ;
			}
		}
		return (int) (0.25 * ret) ;
	}

	public static int getPlotterHeight() {
		int ret = Integer.MAX_VALUE ;
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment() ;
		GraphicsDevice[] gs = ge.getScreenDevices() ;
		for (int i = 0; i < gs.length; i++) {
			DisplayMode dm = gs[i].getDisplayMode() ;
			int height = dm.getHeight() ;
			if (height < ret) {
				ret = height ;
			}
		}
		return (int) (0.2 * ret) ;
	}

}
