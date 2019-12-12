package app.components;

import java.util.concurrent.TimeUnit;
import app.interfaces.appareil.IAjoutAppareil;
import app.interfaces.appareil.IConsommation;
import app.interfaces.appareil.IFrigo;
import app.ports.frigo.FrigoCompteurOutPort;
import app.ports.frigo.FrigoControleurOutPort;
import app.ports.frigo.FrigoInPort;
import app.util.EtatAppareil;
import app.util.ModeFrigo;
import app.util.TypeAppareil;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.ports.PortI;

@OfferedInterfaces(offered = { IFrigo.class })
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
		
		/** TODO faire port entrant pour assembleur */
		
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
		this.tracer.setTitle("Frigo");
		this.tracer.setRelativePosition(0, 1);

		// attributs
		this.type = type;
		this.etat = EtatAppareil.OFF;
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
	 * Adapatation de la temperature en fonction de la temperature cible
	 */
	protected void adaptationTemperature() {
		this.logMessage("regulation de la temperature...");
		/** TODO **/
	}
	
	// ************* Cycle de vie du composant ************* 

	@Override
	public void start() throws ComponentStartException {
		super.start();
		
		this.logMessage("Demarrage du frigo...");

		this.scheduleTask(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try { ((Frigo) this.getTaskOwner()).demandeAjoutControleur("CONSTANTE A METTRE ICI"); }
				catch (Exception e) { throw new RuntimeException(e); }
			}
		}, 1000, TimeUnit.MILLISECONDS);
	}

	@Override
	public void execute() throws Exception {
		super.execute();
		
		this.logMessage("Phase d'execution du frigo.");
		
		this.logMessage("Execution en cours...");
		
		this.scheduleTaskWithFixedDelay(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try { ((Frigo) this.getTaskOwner()).adaptationTemperature(); } 
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
			PortI[] port_controleur = this.findPortsFromInterface(IFrigo.class);
			PortI[] port_consommation = this.findPortsFromInterface(IConsommation.class);
			PortI[] port_ajoutappareil = this.findPortsFromInterface(IAjoutAppareil.class);
			
			port_controleur[0].unpublishPort() ;
			port_consommation[0].unpublishPort();
			port_ajoutappareil[0].unpublishPort();
		} catch (Exception e) { throw new ComponentShutdownException(e); }
		super.shutdownNow();
	}

}
