package app.components;

import java.util.concurrent.TimeUnit;
import app.interfaces.appareil.IAjoutAppareil;
import app.interfaces.appareil.IConsommation;
import app.interfaces.appareil.IFrigo;
import app.interfaces.generateur.IEntiteDynamique;
import app.ports.frigo.FrigoAssembleurInPort;
import app.ports.frigo.FrigoCompteurOutPort;
import app.ports.frigo.FrigoControleurOutPort;
import app.ports.frigo.FrigoInPort;
import app.util.EtatAppareil;
import app.util.ModeFrigo;
import app.util.TypeAppareil;
import app.util.URI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.ports.PortI;

@OfferedInterfaces(offered = { IFrigo.class, IEntiteDynamique.class })
@RequiredInterfaces(required = { IAjoutAppareil.class, IConsommation.class })
public class Frigo extends AbstractComponent {
	
	/** port sortant permettant a l'appareil de s'inscrire sur la liste des appareil du controleur */
	protected FrigoControleurOutPort controleur_OUTPORT;
	
	/** port sortant permettant au compteur de recupere la consommation du frigo */
	protected FrigoCompteurOutPort consommation_OUTPORT;

	protected TypeAppareil type;
	protected EtatAppareil etat;
	protected ModeFrigo lumiere_refrigerateur;
	protected ModeFrigo lumiere_congelateur;

	protected Double congelateur_temperature_cible;
	protected Double refrigerateur_temperature_cible;
	protected Double consommation;

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
		
		this.createNewExecutorService(URI.POOL_ACTION_FRIGO_URI.getURI(), 5, false) ;
		
		// affichage
		this.tracer.setTitle("Frigo");
		this.tracer.setRelativePosition(0, 1);
		this.toggleTracing();

		// attributs
		this.type = type;
		this.etat = EtatAppareil.OFF;
		this.refrigerateur_temperature_cible = 3.0;
		this.congelateur_temperature_cible = -10.0;
		this.consommation = 55.0;
		this.lumiere_refrigerateur = ModeFrigo.LIGHT_OFF;
		this.lumiere_congelateur = ModeFrigo.LIGHT_OFF;
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

	public void setTemperature_Refrigerateur(double temperature) throws Exception {
		this.refrigerateur_temperature_cible = temperature;
	}

	public void setTemperature_Congelateur(double temperature) throws Exception {
		this.congelateur_temperature_cible = temperature;
	}

	public void setLumiere_Refrigerateur(ModeFrigo mf) throws Exception {
		this.lumiere_refrigerateur = mf;
	}

	public void setLumiere_Congelateur(ModeFrigo mf) throws Exception {
		this.lumiere_congelateur = mf;
	}

	/**
	 * Actions du frigo pendant l'execution
	 */
	protected void runningAndPrint() {
		this.logMessage("Action du frigo...");
		/** TODO **/
		
		if(lumiere_refrigerateur == ModeFrigo.LIGHT_ON) {
			if(lumiere_congelateur == ModeFrigo.LIGHT_ON) {
				consommation = 4.0;
			} else {
				consommation = 2.0;
			}
		} else if(lumiere_congelateur == ModeFrigo.LIGHT_ON) {
			if(lumiere_refrigerateur == ModeFrigo.LIGHT_ON) {
				consommation = 4.0;
			} else {
				consommation = 2.0;
			}
		} else {
			consommation = 0.0;
		}
	}
	
	// ************* Cycle de vie du composant ************* 

	@Override
	public void start() throws ComponentStartException {
		super.start();
		this.logMessage("Demarrage du frigo...");
	}

	@Override
	public void execute() throws Exception {
		super.execute();
				
		this.logMessage("Phase d'execution du frigo.");
		
		this.logMessage("Execution en cours...");
		
		this.scheduleTaskWithFixedDelay(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try { ((Frigo) this.getTaskOwner()).envoyerConsommation(URI.FRIGO_URI.getURI(), consommation); } 
				catch (Exception e) { throw new RuntimeException(e); }
			}
		}, 2000, 1000, TimeUnit.MILLISECONDS);
		
		this.scheduleTaskWithFixedDelay(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try { ((Frigo) this.getTaskOwner()).runningAndPrint(); } 
				catch (Exception e) { throw new RuntimeException(e); }
			}
		}, 2000, 1000, TimeUnit.MILLISECONDS);
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
			PortI[] port_controleur = this.findPortsFromInterface(IFrigo.class);
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
