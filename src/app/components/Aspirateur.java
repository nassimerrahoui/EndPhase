package app.components;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import app.interfaces.appareil.IAjoutAppareil;
import app.interfaces.appareil.IConsommation;
import app.interfaces.appareil.IAspirateur;
import app.interfaces.generateur.IComposantDynamique;
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
import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentStateAccessI;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.ports.PortI;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import simulator.models.AspirateurCoupledModel;
import simulator.models.AspirateurModel;
import simulator.plugins.AspirateurSimulatorPlugin;

@OfferedInterfaces(offered = { IAspirateur.class, IComposantDynamique.class })
@RequiredInterfaces(required = { IAjoutAppareil.class, IConsommation.class })
public class Aspirateur 
	extends AbstractCyPhyComponent 
	implements EmbeddingComponentStateAccessI {

	/** port sortant permettant a l'appareil de s'inscrire sur la liste des appareil du controleur */
	protected AspirateurControleurOutPort controleur_OUTPORT;
	
	/** port sortant permettant au compteur de recupere la consommation de l'aspirateur */
	protected AspirateurCompteurOutPort consommation_OUTPORT;

	protected TypeAppareil type;
	protected ModeAspirateur etat;
	protected Double consommation;
	
	protected AspirateurSimulatorPlugin asp;

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
		this.tracer.setRelativePosition(2, 1);
		
		// attributs
		this.etat = ModeAspirateur.OFF;
		this.consommation = 0.0;
		this.type = type;
		
		this.initialise();
	}

	public void demandeAjoutControleur(String uri) throws Exception {
		this.controleur_OUTPORT.demandeAjoutControleur(uri);
	}

	public void envoyerConsommation(String uri, double consommation) throws Exception {
		this.consommation_OUTPORT.envoyerConsommation(uri, consommation);
	}

	public void setModeAspirateur(ModeAspirateur etat) throws Exception {
		this.etat = etat;
	}
	
	/**
	 * Gerer et afficher ce qui se passe pendant un mode
	 */
	public void runningAndPrint() {
		/** TODO Redefinir toString a la place de name */
		this.logMessage("Mode actuel : " + etat.name());
		
		/** TODO code pour gerer ce qui se passe pendant un mode */
		if(etat == ModeAspirateur.VEILLE) {
			consommation = 0.0;
		} else if(etat == ModeAspirateur.PERFORMANCE_REDUITE) {
			consommation = 2.0;
		} else if(etat == ModeAspirateur.PERFORMANCE_MAXIMALE) {
			consommation = 4.0;
		} else if(etat == ModeAspirateur.OFF) {
			consommation = 0.0;
		}
	}
	
	// ************* Cycle de vie du composant ************* 

	@Override
	public void start() throws ComponentStartException {
		super.start();
		this.logMessage("Demarrage de l'aspirateur...");
	}
	
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
		simParams.put("componentRef", this) ;
		this.asp.setSimulationRunParameters(simParams) ;

		this.runTask(
				new AbstractComponent.AbstractTask() {
					@Override
					public void run() {
						try {
							asp.doStandAloneSimulation(0.0, 20000.0) ;
						} catch (Exception e) {
							throw new RuntimeException(e) ;
						}
					}
				}) ;
		Thread.sleep(10L) ;
		for (int i = 0 ; i < 1000000 ; i++) {
			this.logMessage("Aspirateur " +
				this.asp.getModelStateValue(AspirateurModel.URI, "state") + " " +
				this.asp.getModelStateValue(AspirateurModel.URI, "intensity")) ;
			this.etat = (ModeAspirateur) this.asp.getModelStateValue(AspirateurModel.URI, "state");
			this.consommation =(Double) this.asp.getModelStateValue(AspirateurModel.URI, "intensity");
			Thread.sleep(10L);
		}
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
		if(name.equals("state")) {
			return etat;
		} else if(name.equals("intensity")) {
			return consommation;
		}
		return null;
	}
	
	protected void initialise() throws Exception {
		Architecture localArchitecture = this.createLocalArchitecture(null) ;
		this.asp = new AspirateurSimulatorPlugin() ;
		this.asp.setPluginURI(localArchitecture.getRootModelURI()) ;
		this.asp.setSimulationArchitecture(localArchitecture) ;
		this.installPlugin(this.asp) ;
	}
}