package app.components;

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
import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentStateAccessI;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.ports.PortI;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import simulator.models.frigo.FrigoModel;
import simulator.plugins.FrigoSimulatorPlugin;

@OfferedInterfaces(offered = { IFrigo.class, IComposantDynamique.class })
@RequiredInterfaces(required = { IAjoutAppareil.class, IConsommation.class })
public class Frigo	
	extends AbstractCyPhyComponent 
	implements EmbeddingComponentStateAccessI {
	
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
		
		// port entrant permettant a l'assembleur d'effectuer d'integrer l'entite au logement
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
		this.tracer.setRelativePosition(0, 1);

		// attributs
		this.type = type;
		this.etat = ModeFrigo.OFF;
		this.refrigerateur_temperature_cible = 3.0;
		this.congelateur_temperature_cible = -10.0;
		this.consommation = 55.0;
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
		
		if(this.etat == ModeFrigo.OFF) {
			consommation = 0.0;
		} else if(this.etat == ModeFrigo.LIGHT_OFF) {
			consommation = 2.0;
		} else if(this.etat == ModeFrigo.LIGHT_ON) {
			consommation = 3.0;
		}
		
		this.logMessage("Temperature cible refrigerateur : " + refrigerateur_temperature_cible);
		this.logMessage("Temperature cible congelateur : " + congelateur_temperature_cible);
		this.logMessage("...");
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
		return null;
	}
	

	@Override
	public Object getEmbeddingComponentStateValue(String name) throws Exception {
		if(name.equals("state")) {
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
		this.asp = new FrigoSimulatorPlugin() ;
		this.asp.setPluginURI(localArchitecture.getRootModelURI()) ;
		this.asp.setSimulationArchitecture(localArchitecture) ;
		this.installPlugin(this.asp) ;
	}
}
