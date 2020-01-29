package app.components;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import app.CVM;
import app.interfaces.appareil.IAjoutAppareil;
import app.interfaces.appareil.IConsommation;
import app.interfaces.assembleur.IComposantDynamique;
import app.interfaces.appareil.IAspirateur;
import app.ports.aspirateur.AspirateurAssembleurInPort;
import app.ports.aspirateur.AspirateurCompteurOutPort;
import app.ports.aspirateur.AspirateurControleurOutPort;
import app.ports.aspirateur.AspirateurInPort;
import app.util.ModeAspirateur;
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
import simulator.models.aspirateur.AspirateurCoupledModel;
import simulator.models.aspirateur.AspirateurModel;
import simulator.plugins.AspirateurSimulatorPlugin;

@OfferedInterfaces(offered = { IAspirateur.class, IComposantDynamique.class })
@RequiredInterfaces(required = { IAjoutAppareil.class, IConsommation.class })


public class Aspirateur 
	extends AbstractCyPhyComponent 
	implements EmbeddingComponentAccessI {

	/** port sortant permettant a l'appareil de s'inscrire sur la liste des appareil du controleur */
	protected AspirateurControleurOutPort controleur_OUTPORT;
	
	/** port sortant permettant au compteur de recupere la consommation de l'aspirateur */
	protected AspirateurCompteurOutPort consommation_OUTPORT;

	/** Gestion de priorite pour les decisions du controleur*/
	protected TypeAppareil type;
	
	/** Etat actuel de l'appareil */
	protected ModeAspirateur etat;
	
	/** Consommation en Watts par l'appareil */
	protected Double consommation;
	
	protected AspirateurSimulatorPlugin asp;
	
	public static int ORIGIN_X = CVM.plotX;
	public static int ORIGIN_Y = CVM.plotY;

	protected Aspirateur(
			String ASPIRATEUR_URI, 
			String ASPIRATEUR_COMPTEUR_OP_URI,
			String ASPIRATEUR_CONTROLEUR_OP_URI,
			int nbThreads, int nbSchedulableThreads, 
			TypeAppareil type) throws Exception {
		super(ASPIRATEUR_URI, nbThreads, nbSchedulableThreads);

		controleur_OUTPORT = new AspirateurControleurOutPort(ASPIRATEUR_CONTROLEUR_OP_URI,this);
		consommation_OUTPORT = new AspirateurCompteurOutPort(ASPIRATEUR_COMPTEUR_OP_URI,this);
		
		// port entrant permettant au controleur d'effectuer des actions sur l'aspirateur
		AspirateurInPort action_INPORT = new AspirateurInPort(this);
		
		// port entrant permettant a l'assembleur d'effectuer d'integrer l'entite au logement
		AspirateurAssembleurInPort launch_INPORT = new AspirateurAssembleurInPort(this);
		
		controleur_OUTPORT.publishPort();
		consommation_OUTPORT.publishPort();
		action_INPORT.publishPort();
		launch_INPORT.publishPort();
		
		if (AbstractCVM.isDistributed) {
			this.executionLog.setDirectory(System.getProperty("user.dir")) ;
		} else {
			this.executionLog.setDirectory(System.getProperty("user.home")) ;
		}
		
		this.createNewExecutorService(URI.POOL_ACTION_ASPIRATEUR_URI.getURI(), 5, false) ;
		
		// affichage
		this.tracer.setTitle("Aspirateur");
		this.tracer.setRelativePosition(0, 1);
		
		// attributs
		this.etat = ModeAspirateur.OFF;
		this.consommation = 0.0;
		this.type = type;
		
		this.initialise();
	}

	/**
	 * Ajoute l'URI de l'appareil a la map des appareils du controleur
	 * @param uri
	 * @throws Exception
	 */
	public void demandeAjoutControleur(String uri) throws Exception {
		this.controleur_OUTPORT.demandeAjoutControleur(uri, getClass().getName(), this.type);
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
	 * Modifie l'etat de l'aspirateur
	 * @param etat
	 * @throws Exception
	 */
	public void setModeAspirateur(ModeAspirateur etat) throws Exception {
		this.etat = etat;
	}
	
	/**
	 * Gerer et afficher ce qui se passe pendant un mode
	 */
	public void runningAndPrint() {
		/** TODO */
	}
	
	// ************* Cycle de vie du composant ************* 

	@Override
	public void start() throws ComponentStartException {
		super.start();
		this.logMessage("Demarrage de l'aspirateur...");
	}
	
	/**
	 * Execution depuis l'assembleur
	 * @throws Exception
	 */
	public void dynamicExecute() throws Exception {

		this.logMessage("Phase d'execution de l'aspirateur.");
		
		this.logMessage("Execution en cours...");
		
		this.scheduleTaskWithFixedDelay(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try { ((Aspirateur) this.getTaskOwner()).runningAndPrint(); } 
				catch (Exception e) { throw new RuntimeException(e); }
			}
		}, 2000, 1000, TimeUnit.MILLISECONDS);
		
		this.scheduleTaskWithFixedDelay(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try { ((Aspirateur) this.getTaskOwner()).envoyerConsommation(URI.ASPIRATEUR_URI.getURI(), consommation); } 
				catch (Exception e) { throw new RuntimeException(e); }
			}
		}, 4000, 1000, TimeUnit.MILLISECONDS);
		
		execute();
	}
	
	@Override
	public void execute() throws Exception {
		SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 10L ;

		HashMap<String,Object> simParams = new HashMap<String,Object>() ;
		simParams.put(AspirateurModel.URI + " : " + AspirateurModel.COMPONENT_REF, this);
		
		simParams.put(AspirateurModel.URI + " : " + AspirateurModel.POWER_PLOTTING_PARAM_NAME, new PlotterDescription(
				"Consommation Aspirateur", 
				"Temps (sec)", 
				"Consommation (Watt)", 
				ORIGIN_X - getPlotterWidth(),
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
					((Aspirateur) this.getTaskOwner()).consommation = (Double) ((Aspirateur) this.getTaskOwner()).asp.getModelStateValue(AspirateurModel.URI, "consommation");
					((Aspirateur) this.getTaskOwner()).logMessage("Mode : " + etat);
					((Aspirateur) this.getTaskOwner()).logMessage("Consommation : " + consommation);
				} catch (Exception e) { e.printStackTrace(); }
			}
		}, 4000, 1000, TimeUnit.MILLISECONDS);
	}
	
	@Override
	public void finalise() throws Exception {
		this.logMessage("Arret du composant aspirateur...") ;
		super.finalise();
	}
	
	@Override
	public void	shutdown() throws ComponentShutdownException
	{
		try {
			PortI[] port_controleur = this.findPortsFromInterface(IAspirateur.class);
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
			PortI[] port_controleur = this.findPortsFromInterface(IAspirateur.class);
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
		return AspirateurCoupledModel.build();
	}

	@Override
	public Object getEmbeddingComponentStateValue(String name) throws Exception {
		if(name.equals(AspirateurModel.URI + " : state")) {
			return etat;
		} else if(name.equals(AspirateurModel.URI + " : consommation")) {
			return consommation;
		}
		return null;
	}
	
	/**
	 * Installe le plugin
	 * @throws Exception
	 */
	protected void initialise() throws Exception {
		Architecture localArchitecture = this.createLocalArchitecture(null) ;
		this.asp = new AspirateurSimulatorPlugin() ;
		this.asp.setPluginURI(localArchitecture.getRootModelURI()) ;
		this.asp.setSimulationArchitecture(localArchitecture) ;
		this.installPlugin(this.asp) ;
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