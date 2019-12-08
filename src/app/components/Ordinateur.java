package app.components;

import java.util.concurrent.TimeUnit;

import app.interfaces.appareil.IAjoutAppareil;
import app.interfaces.appareil.IConsommation;
import app.interfaces.appareil.IOrdinateur;
import app.ports.ordi.OrdinateurCompteurOutPort;
import app.ports.ordi.OrdinateurControleurOutPort;
import app.ports.ordi.OrdinateurInPort;
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

@OfferedInterfaces(offered = { IOrdinateur.class })
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

	public Ordinateur(String ordiURI, 
			int nbThreads, int nbSchedulableThreads, 
			TypeAppareil type) throws Exception {
		super(ordiURI, nbThreads, nbSchedulableThreads);

		controleur_OUTPORT = new OrdinateurControleurOutPort(this);
		consommation_OUTPORT = new OrdinateurCompteurOutPort(this);
		
		// port entrant permettant au controleur d'effectuer des actions sur l'ordinateur
		OrdinateurInPort action_INPORT = new OrdinateurInPort(this);
		
		this.addPort(controleur_OUTPORT);
		this.addPort(consommation_OUTPORT);
		this.addPort(action_INPORT);
		
		controleur_OUTPORT.publishPort();
		consommation_OUTPORT.publishPort();
		action_INPORT.publishPort();
		
		if (AbstractCVM.isDistributed) {
			this.executionLog.setDirectory(System.getProperty("user.dir")) ;
		} else {
			this.executionLog.setDirectory(System.getProperty("user.home")) ;
		}
		
		/** TODO definir pool de thread */
		
		// affichage
		this.tracer.setTitle("Ordinateur");
		this.tracer.setRelativePosition(2, 1);
		
		// attributs
		this.etat = EtatAppareil.OFF;
		this.consommation = 90.0;
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
	}
	
	// ************* Cycle de vie du composant ************* 

	@Override
	public void start() throws ComponentStartException {
		super.start();
		
		this.logMessage("Demarrage de l'ordinateur...");

		this.scheduleTask(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try { ((Ordinateur) this.getTaskOwner()).demandeAjoutControleur("CONSTANTE URI A METTRE ICI"); }
				catch (Exception e) { throw new RuntimeException(e); }
			}
		}, 1000, TimeUnit.MILLISECONDS);
	}

	@Override
	public void execute() throws Exception {
		super.execute();
		
		this.logMessage("Phase d'execution de l'ordinateur.");
		
		this.logMessage("Passage en Performance reduite.");
		
		this.scheduleTask(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try { ((Ordinateur) this.getTaskOwner()).setMode(ModeOrdinateur.PerformanceReduite); } 
				catch (Exception e) { throw new RuntimeException(e); }
			}
		}, 2000, TimeUnit.MILLISECONDS);
		
		this.logMessage("Execution en cours...");
		
		this.scheduleTaskWithFixedDelay(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try { ((LaveLinge) this.getTaskOwner()).runningAndPrint(); } 
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
			
			port_controleur[0].unpublishPort() ;
			port_consommation[0].unpublishPort();
			port_ajoutappareil[0].unpublishPort();
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
			
			port_controleur[0].unpublishPort() ;
			port_consommation[0].unpublishPort();
			port_ajoutappareil[0].unpublishPort();
		} catch (Exception e) { throw new ComponentShutdownException(e); }
		super.shutdownNow();
	}
}