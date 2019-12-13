package app.components;

import java.util.concurrent.TimeUnit;
import app.interfaces.appareil.IAjoutAppareil;
import app.interfaces.appareil.IConsommation;
import app.interfaces.appareil.ILaveLinge;
import app.interfaces.generateur.IEntiteDynamique;
import app.ports.lavelinge.LaveLingeAssembleurInPort;
import app.ports.lavelinge.LaveLingeCompteurOutPort;
import app.ports.lavelinge.LaveLingeControleurOutPort;
import app.ports.lavelinge.LaveLingeInPort;
import app.util.EtatAppareil;
import app.util.ModeLaveLinge;
import app.util.TemperatureLaveLinge;
import app.util.TypeAppareil;
import app.util.URI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.ports.PortI;

@OfferedInterfaces(offered = { ILaveLinge.class, IEntiteDynamique.class })
@RequiredInterfaces(required = { IAjoutAppareil.class, IConsommation.class })
public class LaveLinge extends AbstractComponent {

	/** port sortant permettant a l'appareil de s'inscrire sur la liste des appareil du controleur */
	protected LaveLingeControleurOutPort controleur_OUTPORT;
	
	/** port sortant permettant au compteur de recupere la consommation du lave-linge */
	protected LaveLingeCompteurOutPort consommation_OUTPORT;

	protected TypeAppareil type;
	protected EtatAppareil etat;
	protected ModeLaveLinge mode;
	
	protected int heure;
	protected int minutes;
	protected Double consommation;
	protected TemperatureLaveLinge temperature;

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
		this.toggleTracing();
		
		// attributs
		this.heure = 0;
		this.minutes = 0;
		this.etat = EtatAppareil.OFF;
		this.consommation = 0.0;
		this.type = type;
		this.temperature = TemperatureLaveLinge.QUARANTE_DEGRES;
		this.mode = ModeLaveLinge.Veille;
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

	public void planifierCycle(int heure, int min) throws Exception {
		/** TODO */
		planifierMode(ModeLaveLinge.Veille, heure, min);
		planifierMode(ModeLaveLinge.Lavage, heure, min+10);
		planifierMode(ModeLaveLinge.Rincage, heure, min+20);
		planifierMode(ModeLaveLinge.Essorage, heure, min+30);
		planifierMode(ModeLaveLinge.Veille, heure, min+40);
	}

	public void planifierMode(ModeLaveLinge ml, int heure, int min) throws Exception {
		if(etat == EtatAppareil.ON) {
			this.heure = heure;
			this.minutes = min;
			this.mode = ml;
		}
	}

	public void setTemperature(TemperatureLaveLinge tl) {
		this.temperature = tl;
	}
	
	/**
	 * Gerer et afficher ce qui se passe pendant un mode
	 */
	public void runningAndPrint() {
		/** TODO Redefinir toString a la place de name */
		this.logMessage("Mode actuel : " + mode.name());
		
		/** TODO code pour gerer ce qui se passe pendant un mode */
		if(mode == ModeLaveLinge.Veille) {
			consommation = 0.0;
		} else if(mode == ModeLaveLinge.Lavage) {
			consommation = 2.0;
		} else if (mode == ModeLaveLinge.Rincage) {
			consommation = 2.0;
		} else if (mode == ModeLaveLinge.Essorage) {
			consommation = 4.0;
		} else if (mode == ModeLaveLinge.Sechage) {
			consommation = 6.0;
		} else if (mode == ModeLaveLinge.Vidange) {
			consommation = 1.0;
		}
	}
	
	// ************* Cycle de vie du composant ************* 

	@Override
	public void start() throws ComponentStartException {
		super.start();
		this.logMessage("Demarrage du lave-linge...");
		
		this.logMessage("Phase d'execution du lave-linge.");
		
		this.logMessage("Planification du lave-linge.");
		
		this.scheduleTaskWithFixedDelay(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try { ((LaveLinge) this.getTaskOwner()).envoyerConsommation(URI.LAVELINGE_URI.getURI(), consommation); } 
				catch (Exception e) { throw new RuntimeException(e); }
			}
		}, 2000, 1000, TimeUnit.MILLISECONDS);
		
		this.scheduleTask(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				/** TODO remplacer heure et minutes pas constante */
				int heure = 7;
				int minutes = 30;
				try { ((LaveLinge) this.getTaskOwner()).planifierCycle(heure, minutes); } 
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
			PortI[] port_controleur = this.findPortsFromInterface(ILaveLinge.class);
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
