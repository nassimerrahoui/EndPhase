package app.components;

import java.util.concurrent.TimeUnit;
import app.interfaces.appareil.IAjoutAppareil;
import app.interfaces.appareil.IConsommation;
import app.interfaces.appareil.IOrdinateur;
import app.interfaces.generateur.IEntiteDynamique;
import app.ports.ordinateur.OrdinateurAssembleurInPort;
import app.ports.ordinateur.OrdinateurCompteurOutPort;
import app.ports.ordinateur.OrdinateurControleurOutPort;
import app.ports.ordinateur.OrdinateurInPort;
import app.util.EtatAppareil;
import app.util.ModeOrdinateur;
import app.util.TypeAppareil;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.ports.PortI;

@OfferedInterfaces(offered = { IOrdinateur.class, IEntiteDynamique.class })
@RequiredInterfaces(required = { IAjoutAppareil.class, IConsommation.class })
public class Ordinateur extends AbstractComponent {

	/** port sortant permettant a l'appareil de s'inscrire sur la liste des appareil du controleur */
	protected OrdinateurControleurOutPort controleur_OUTPORT;
	
	/** port sortant permettant au compteur de recupere la consommation de l'ordinateur */
	protected OrdinateurCompteurOutPort consommation_OUTPORT;

	protected TypeAppareil type;
	protected EtatAppareil etat;
	protected ModeOrdinateur mode;
	protected Double consommation;

	public Ordinateur(
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
		
		this.addPort(controleur_OUTPORT);
		this.addPort(consommation_OUTPORT);
		this.addPort(action_INPORT);
		this.addPort(launch_INPORT);
		
		controleur_OUTPORT.publishPort();
		consommation_OUTPORT.publishPort();
		action_INPORT.publishPort();
		launch_INPORT.publishPort();
		
		if (AbstractCVM.isDistributed) {
			this.executionLog.setDirectory(System.getProperty("user.dir")) ;
		} else {
			this.executionLog.setDirectory(System.getProperty("user.home")) ;
		}
		
		/** TODO definir pool de thread */
		
		// affichage
		this.tracer.setTitle("Ordinateur");
		this.tracer.setRelativePosition(2, 1);
		this.toggleTracing();
		
		// attributs
		this.etat = EtatAppareil.OFF;
		this.mode = ModeOrdinateur.Veille;
		this.consommation = 0.0;
		this.type = type;
	}

	public void demandeAjoutControleur(String uri) throws Exception {
		this.controleur_OUTPORT.demandeAjoutControleur(uri);
	}

	public void envoyerConsommation(String uri, double consommation) throws Exception {
		this.consommation_OUTPORT.envoyerConsommation(uri, consommation);
	}

	public void setEtatAppareil(EtatAppareil etat) throws Exception {
		this.etat = etat;
	}

	public void setMode(ModeOrdinateur mo) throws Exception {
		this.mode = mo;
	}
	
	/**
	 * Gerer et afficher ce qui se passe pendant un mode
	 */
	public void runningAndPrint() {
		/** TODO Redefinir toString a la place de name */
		this.logMessage("Mode actuel : " + mode.name());
		
		/** TODO code pour gerer ce qui se passe pendant un mode */
		if(mode == ModeOrdinateur.Veille) {
			consommation = 0.0;
		} else if(mode == ModeOrdinateur.PerformanceReduite) {
			consommation = 2.0;
		} else if(mode == ModeOrdinateur.PerformanceMaximale) {
			consommation = 4.0;
		}
	}
	
	// ************* Cycle de vie du composant ************* 

	@Override
	public void start() throws ComponentStartException {
		super.start();
		this.logMessage("Demarrage de l'ordinateur...");
	}

	@Override
	public void execute() throws Exception {
		super.execute();
		
		this.logMessage("Phase d'execution de l'ordinateur.");
		
		this.logMessage("Passage en Performance reduite.");
		
		this.logMessage("Execution en cours...");
		
		this.scheduleTaskWithFixedDelay(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try { ((Ordinateur) this.getTaskOwner()).runningAndPrint(); } 
				catch (Exception e) { throw new RuntimeException(e); }
			}
		}, 4000, 1000, TimeUnit.MILLISECONDS);
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
			PortI[] port_assembleur = this.findPortsFromInterface(IEntiteDynamique.class);
			
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
			PortI[] port_assembleur = this.findPortsFromInterface(IEntiteDynamique.class);
			
			port_controleur[0].unpublishPort() ;
			port_consommation[0].unpublishPort();
			port_ajoutappareil[0].unpublishPort();
			port_assembleur[0].unpublishPort();
		} catch (Exception e) { throw new ComponentShutdownException(e); }
		super.shutdownNow();
	}
}