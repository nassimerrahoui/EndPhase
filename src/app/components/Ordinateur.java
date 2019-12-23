package app.components;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import app.interfaces.appareil.IAjoutAppareil;
import app.interfaces.appareil.IConsommation;
import app.interfaces.appareil.IOrdinateur;
import app.interfaces.generateur.IComposantDynamique;
import app.ports.ordinateur.OrdinateurAssembleurInPort;
import app.ports.ordinateur.OrdinateurCompteurOutPort;
import app.ports.ordinateur.OrdinateurControleurOutPort;
import app.ports.ordinateur.OrdinateurInPort;
import app.util.ModeOrdinateur;
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
import simulator.models.OrdinateurCoupledModel;
import simulator.models.OrdinateurModel;
import simulator.plugins.OrdinateurSimulatorPlugin;

@OfferedInterfaces(offered = { IOrdinateur.class, IComposantDynamique.class })
@RequiredInterfaces(required = { IAjoutAppareil.class, IConsommation.class })
public class Ordinateur 
	extends AbstractCyPhyComponent 
	implements EmbeddingComponentStateAccessI {

	/** port sortant permettant a l'appareil de s'inscrire sur la liste des appareil du controleur */
	protected OrdinateurControleurOutPort controleur_OUTPORT;
	
	/** port sortant permettant au compteur de recupere la consommation de l'ordinateur */
	protected OrdinateurCompteurOutPort consommation_OUTPORT;

	protected TypeAppareil type;
	protected ModeOrdinateur etat;
	protected Double consommation;
	
	protected OrdinateurSimulatorPlugin asp;

	protected Ordinateur(
			String ORDINATEUR_URI, 
			String ORDINATEUR_COMPTEUR_OP_URI,
			String ORDINATEUR_CONTROLEUR_OP_URI,
			int nbThreads, int nbSchedulableThreads, 
			TypeAppareil type) throws Exception {
		super(ORDINATEUR_URI, nbThreads, nbSchedulableThreads);

		controleur_OUTPORT = new OrdinateurControleurOutPort(ORDINATEUR_CONTROLEUR_OP_URI,this);
		consommation_OUTPORT = new OrdinateurCompteurOutPort(ORDINATEUR_COMPTEUR_OP_URI,this);
		
		// port entrant permettant au controleur d'effectuer des actions sur l'ordinateur
		OrdinateurInPort action_INPORT = new OrdinateurInPort(this);
		
		// port entrant permettant a l'assembleur d'effectuer d'integrer l'entite au logement
		OrdinateurAssembleurInPort launch_INPORT = new OrdinateurAssembleurInPort(this);
		
		controleur_OUTPORT.publishPort();
		consommation_OUTPORT.publishPort();
		action_INPORT.publishPort();
		launch_INPORT.publishPort();
		
		if (AbstractCVM.isDistributed) {
			this.executionLog.setDirectory(System.getProperty("user.dir")) ;
		} else {
			this.executionLog.setDirectory(System.getProperty("user.home")) ;
		}
		
		this.createNewExecutorService(URI.POOL_ACTION_ORDINATEUR_URI.getURI(), 5, false) ;
		
		// affichage
		this.tracer.setTitle("Ordinateur");
		this.tracer.setRelativePosition(2, 1);
		
		// attributs
		this.etat = ModeOrdinateur.OFF;
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

	public void setModeOrdinateur(ModeOrdinateur etat) throws Exception {
		this.etat = etat;
	}
	
	/**
	 * Gerer et afficher ce qui se passe pendant un mode
	 */
	public void runningAndPrint() {
		/** TODO Redefinir toString a la place de name */
		this.logMessage("Mode actuel : " + etat.name());
		
		/** TODO code pour gerer ce qui se passe pendant un mode */
		if(etat == ModeOrdinateur.VEILLE) {
			consommation = 0.0;
		} else if(etat == ModeOrdinateur.PERFORMANCE_REDUITE) {
			consommation = 2.0;
		} else if(etat == ModeOrdinateur.PERFORMANCE_MAXIMALE) {
			consommation = 4.0;
		} else if(etat == ModeOrdinateur.OFF) {
			consommation = 0.0;
		}
	}
	
	// ************* Cycle de vie du composant ************* 

	@Override
	public void start() throws ComponentStartException {
		super.start();
		this.logMessage("Demarrage de l'ordinateur...");
	}
	
	public void dynamicExecute() throws Exception {

		this.logMessage("Phase d'execution de l'ordinateur.");
		
		this.logMessage("Execution en cours...");
		
		this.scheduleTaskWithFixedDelay(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try { ((Ordinateur) this.getTaskOwner()).runningAndPrint(); } 
				catch (Exception e) { throw new RuntimeException(e); }
			}
		}, 2000, 1000, TimeUnit.MILLISECONDS);
		
		this.scheduleTaskWithFixedDelay(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try { ((Ordinateur) this.getTaskOwner()).envoyerConsommation(URI.ORDINATEUR_URI.getURI(), consommation); } 
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
			this.logMessage("Ordinateur " +
				this.asp.getModelStateValue(OrdinateurModel.URI, "state") + " " +
				this.asp.getModelStateValue(OrdinateurModel.URI, "intensity")) ;
			this.etat = (ModeOrdinateur) this.asp.getModelStateValue(OrdinateurModel.URI, "state");
			Thread.sleep(10L);
		}
	}
	
	@Override
	public void finalise() throws Exception {
		this.logMessage("Arret du composant ordinateur...") ;
		super.finalise();
	}
	
	@Override
	public void	shutdown() throws ComponentShutdownException
	{
		try {
			PortI[] port_controleur = this.findPortsFromInterface(IOrdinateur.class);
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
			PortI[] port_controleur = this.findPortsFromInterface(IOrdinateur.class);
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
		return OrdinateurCoupledModel.build();
	}

	@Override
	public Object getEmbeddingComponentStateValue(String name) throws Exception {
		return etat;
	}
	
	protected void initialise() throws Exception {
		Architecture localArchitecture = this.createLocalArchitecture(null) ;
		this.asp = new OrdinateurSimulatorPlugin() ;
		this.asp.setPluginURI(localArchitecture.getRootModelURI()) ;
		this.asp.setSimulationArchitecture(localArchitecture) ;
		this.installPlugin(this.asp) ;
	}
}