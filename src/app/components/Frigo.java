package app.components;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import app.interfaces.appareil.IAjoutAppareil;
import app.interfaces.appareil.IConsommation;
import app.interfaces.appareil.IFrigo;
import app.interfaces.assembleur.IComposantDynamique;
import app.ports.frigo.FrigoAssembleurInPort;
import app.ports.frigo.FrigoCompteurOutPort;
import app.ports.frigo.FrigoControleurOutPort;
import app.ports.frigo.FrigoInPort;
import app.util.ModeFrigo;
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
import simulator.models.frigo.FrigoCoupledModel;
import simulator.models.frigo.FrigoModel;
import simulator.plugins.FrigoSimulatorPlugin;

@OfferedInterfaces(offered = { IFrigo.class, IComposantDynamique.class })
@RequiredInterfaces(required = { IAjoutAppareil.class, IConsommation.class })
public class Frigo	
	extends AbstractCyPhyComponent 
	implements EmbeddingComponentAccessI {
	
	/** port sortant permettant a l'appareil de s'inscrire sur la liste des appareil du controleur */
	protected FrigoControleurOutPort controleur_OUTPORT;
	
	/** port sortant permettant au compteur de recupere la consommation du frigo */
	protected FrigoCompteurOutPort consommation_OUTPORT;

	protected TypeAppareil type;
	protected ModeFrigo etat;

	protected double  refrigerateur_current_temperature;
	protected double  congelateur_current_temperature;
	
	protected double congelateur_temperature_cible;
	protected double refrigerateur_temperature_cible;
	protected double consommation;
	
	protected FrigoSimulatorPlugin asp;
	
	public static int ORIGIN_X = 340 ;
	public static int ORIGIN_Y = 20 ;

	protected Frigo(
			String FRIGO_URI, 
			String FRIGO_COMPTEUR_OP_URI,
			String FRIGO_CONTROLEUR_OP_URI,
			int nbThreads, int nbSchedulableThreads,
			TypeAppareil type) throws Exception {
		super(FRIGO_URI, nbThreads, nbSchedulableThreads);

		controleur_OUTPORT = new FrigoControleurOutPort(FRIGO_CONTROLEUR_OP_URI,this);
		consommation_OUTPORT = new FrigoCompteurOutPort(FRIGO_COMPTEUR_OP_URI,this);
		
		// port entrant permettant au controleur d'effectuer des actions sur le frigo
		FrigoInPort action_INPORT = new FrigoInPort(this);
		
		// port entrant permettant a l'assembleur de deployer le composant
		FrigoAssembleurInPort launch_INPORT = new FrigoAssembleurInPort(this);
		
		controleur_OUTPORT.publishPort();
		consommation_OUTPORT.publishPort();
		action_INPORT.publishPort();
		launch_INPORT.publishPort();
		
		if (AbstractCVM.isDistributed) {
			this.executionLog.setDirectory(System.getProperty("user.dir")) ;
		} else {
			this.executionLog.setDirectory(System.getProperty("user.home")) ;
		}
		
		this.createNewExecutorService(URI.POOL_ACTION_FRIGO_URI.getURI(), 5, false) ;
		
		// affichage
		this.tracer.setTitle("Frigo");
		this.tracer.setRelativePosition(2, 1);

		// attributs
		this.type = type;
		this.etat = ModeFrigo.OFF;
		this.refrigerateur_temperature_cible = 3.0;
		this.congelateur_temperature_cible = -10.0;
		this.consommation = 55.0;
		this.refrigerateur_current_temperature = FrigoModel.AMBIENT_TEMPERATURE;
		
		this.initialise();
	}

	public void demandeAjoutControleur(String uri) throws Exception {
		this.controleur_OUTPORT.demandeAjoutControleur(uri);
	}

	public void envoyerConsommation(String uri, double consommation) throws Exception {
		this.consommation_OUTPORT.envoyerConsommation(uri, consommation);
	}

	public void setModeFrigo(ModeFrigo etat) throws Exception {
		this.etat = etat;
	}

	public void setTemperature_Refrigerateur(double temperature) throws Exception {
		this.refrigerateur_temperature_cible = temperature;
	}

	public void setTemperature_Congelateur(double temperature) throws Exception {
		this.congelateur_temperature_cible = temperature;
	}

	/**
	 * Actions du frigo pendant l'execution
	 */
	protected void runningAndPrint() {
		
		/** TODO */
	}
	
	// ************* Cycle de vie du composant ************* 

	@Override
	public void start() throws ComponentStartException {
		super.start();
		this.logMessage("Demarrage du frigo...");
	}
	
	
	public void dynamicExecute() throws Exception {
		
		this.logMessage("Phase d'execution du frigo.");
		
		this.logMessage("Execution en cours...");
		
		this.scheduleTaskWithFixedDelay(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try { ((Frigo) this.getTaskOwner()).runningAndPrint(); } 
				catch (Exception e) { throw new RuntimeException(e); }
			}
		}, 2000, 4000, TimeUnit.MILLISECONDS);
		
		this.scheduleTaskWithFixedDelay(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try { ((Frigo) this.getTaskOwner()).envoyerConsommation(URI.FRIGO_URI.getURI(), consommation); } 
				catch (Exception e) { throw new RuntimeException(e); }
			}
		}, 2500, 1000, TimeUnit.MILLISECONDS);
		
		execute();
	}
	
	@Override
	public void execute() throws Exception {
		SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 10L ;
		
		HashMap<String,Object> simParams = new HashMap<String,Object>() ;
		simParams.put(FrigoModel.URI + " : " + FrigoModel.COMPONENT_REF, this);
		simParams.put(FrigoModel.URI + " : " + FrigoModel.POWER_PLOTTING_PARAM_NAME, new PlotterDescription(
				"Frigo Model - Consommation",
				"Time (sec)",
				"Consommation (W)",
				ORIGIN_X + getPlotterWidth(),
		  		ORIGIN_Y,
		  		getPlotterWidth(),
		  		getPlotterHeight())) ;
		simParams.put(FrigoModel.URI + " : " + FrigoModel.STATE_PLOTTING_PARAM_NAME, new PlotterDescription(
				"Frigo Model - Etat",
				"Time (sec)",
				"Etat",
				ORIGIN_X + 2 * getPlotterWidth(),
		  		ORIGIN_Y,
				getPlotterWidth(),
				getPlotterHeight())) ;
		simParams.put(FrigoModel.URI + " : " + FrigoModel.TEMPERATURE_PLOTTING_PARAM_NAME, new PlotterDescription(
				"Frigo Model - Temperature",
				"Time (sec)",
				"Temperature (°C)",
				ORIGIN_X + 2 * getPlotterWidth(),
		  		ORIGIN_Y + getPlotterHeight(),
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
					((Frigo) this.getTaskOwner()).etat = (ModeFrigo) ((Frigo) this.getTaskOwner()).asp.getModelStateValue(FrigoModel.URI, "state");
					((Frigo) this.getTaskOwner()).consommation = (double) ((Frigo) this.getTaskOwner()).asp.getModelStateValue(FrigoModel.URI, "consommation");
					((Frigo) this.getTaskOwner()).refrigerateur_current_temperature = (double) ((Frigo) this.getTaskOwner()).asp.getModelStateValue(FrigoModel.URI, "temperature");
					((Frigo) this.getTaskOwner()).logMessage("Mode : " + etat);
					((Frigo) this.getTaskOwner()).logMessage("Consommation : " + Math.round(consommation));
					Thread.sleep(10L);
				} catch (Exception e) { e.printStackTrace(); }
			}
		}, 2500, 1000, TimeUnit.MILLISECONDS);
	}
	
	@Override
	public void finalise() throws Exception {
		this.logMessage("Arret du composant frigo...") ;
		super.finalise();
	}
	
	@Override
	public void	shutdown() throws ComponentShutdownException
	{
		try {
			PortI[] port_controleur = this.findPortsFromInterface(IFrigo.class);
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
			PortI[] port_controleur = this.findPortsFromInterface(IFrigo.class);
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
		return FrigoCoupledModel.build();
	}

	@Override
	public Object getEmbeddingComponentStateValue(String name) throws Exception {
		if(name.equals(FrigoModel.URI + " : state")) {
			return etat;
		} else if(name.equals(FrigoModel.URI + " : consommation")) {
			return consommation;
		} else if(name.equals(FrigoModel.URI + " : refrigerateur_temperature")) {
			return refrigerateur_current_temperature;
		} else if(name.equals(FrigoModel.URI + " : refrigerateur_temperature_cible")) {
			return refrigerateur_temperature_cible;
		}
		return null;
	}
	
	
	
	protected void initialise() throws Exception {
		
		Architecture localArchitecture = this.createLocalArchitecture(null) ;
		this.asp = new FrigoSimulatorPlugin();
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
