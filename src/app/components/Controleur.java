package app.components;

import java.util.Vector;
import java.util.concurrent.TimeUnit;
import app.interfaces.appareil.IAjoutAppareil;
import app.interfaces.appareil.IConsommation;
import app.interfaces.appareil.ILaveLinge;
import app.interfaces.controleur.IControleCompteur;
import app.interfaces.controleur.IControleFrigo;
import app.interfaces.controleur.IControleLaveLinge;
import app.interfaces.controleur.IControleOrdinateur;
import app.interfaces.controleur.IControleur;
import app.ports.controleur.ControleurBatterieOutPort;
import app.ports.controleur.ControleurCompteurOutPort;
import app.ports.controleur.ControleurFrigoOutPort;
import app.ports.controleur.ControleurLaveLingeOutPort;
import app.ports.controleur.ControleurOrdiOutPort;
import app.ports.controleur.ControleurPanneauoOutPort;
import app.util.EtatAppareil;
import app.util.EtatUniteProduction;
import app.util.ModeFrigo;
import app.util.ModeLaveLinge;
import app.util.ModeOrdinateur;
import app.util.TemperatureLaveLinge;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.ports.PortI;

@OfferedInterfaces(offered = { IControleur.class })
@RequiredInterfaces(required = { 
		IControleCompteur.class, 
		IControleFrigo.class, 
		IControleLaveLinge.class, 
		IControleOrdinateur.class })

public class Controleur extends AbstractComponent {
	
	protected ControleurFrigoOutPort frigo_OUTPORT;
	protected ControleurLaveLingeOutPort lavelinge_OUTPORT;
	protected ControleurOrdiOutPort ordinateur_OUTPORT;
	protected ControleurPanneauoOutPort panneausolaire_OUTPORT;
	protected ControleurBatterieOutPort batterie_OUTPORT;
	protected ControleurCompteurOutPort compteur_OUTPORT;

	protected Vector<String> unitesProduction = new Vector<>();
	protected Vector<String> appareils = new Vector<>();

	public Controleur(String controleurURI, int nbThreads, int nbSchedulableThreads) throws Exception {
		super(controleurURI, nbThreads, nbSchedulableThreads);

		if (AbstractCVM.isDistributed) {
			this.executionLog.setDirectory(System.getProperty("user.dir")) ;
		} else {
			this.executionLog.setDirectory(System.getProperty("user.home")) ;
		}
		
		/** TODO definir pool de thread */
		
		// affichage
		this.tracer.setTitle("Controleur");
		this.tracer.setRelativePosition(1, 0);
	}
	
	// ******* Services requis pour allumer ou eteindre des appareils *********

	public void envoyerEtatFrigo(EtatAppareil etat) throws Exception {
		this.frigo_OUTPORT.envoyerEtatAppareil(etat);
	}
	
	public void envoyerEtatLaveLinge(EtatAppareil etat) throws Exception {
		this.lavelinge_OUTPORT.envoyerEtatAppareil(etat);
	}
	
	public void envoyerEtatOrdinateur(EtatAppareil etat) throws Exception {
		this.ordinateur_OUTPORT.envoyerEtatAppareil(etat);
	}

	// ******* Services requis pour allumer ou eteindre des unites de production *********
	
	public void envoyerEtatPanneauSolaire(EtatUniteProduction etat) throws Exception {
		this.panneausolaire_OUTPORT.envoyerEtatUniteProduction(etat);	
	}
	
	public void envoyerEtatBatterie(EtatUniteProduction etat) throws Exception {
		this.batterie_OUTPORT.envoyerEtatUniteProduction(etat);	
	}

	// ******* Services requis pour effectuer des actions sur lave-linge *********
	
	public void envoyerPlanificationCycle(int heure, int minutes) throws Exception {
		this.lavelinge_OUTPORT.envoyerPlanificationCycle(heure, minutes);		
	}

	public void envoyerPlanificationMode(ModeLaveLinge ml, int heure, int minutes) throws Exception {
		this.lavelinge_OUTPORT.envoyerPlanificationMode(ml, heure, minutes);
	}

	public void envoyerTemperature(TemperatureLaveLinge tl) throws Exception {
		this.lavelinge_OUTPORT.envoyerTemperature(tl);
	}
	
	// ******* Services requis pour effectuer des actions sur frigo *********

	public void envoyerTemperature_Refrigerateur(double temperature) throws Exception {
		this.frigo_OUTPORT.envoyerTemperature_Refrigerateur(temperature);
	}

	public void envoyerTemperature_Congelateur(double temperature) throws Exception {
		this.frigo_OUTPORT.envoyerTemperature_Congelateur(temperature);
	}

	public void envoyerLumiere_Refrigerateur(ModeFrigo mf) throws Exception {
		this.frigo_OUTPORT.envoyerLumiere_Refrigerateur(mf);
	}

	public void envoyerLumiere_Congelateur(ModeFrigo mf) throws Exception {
		this.frigo_OUTPORT.envoyerLumiere_Congelateur(mf);		
	}
	
	// ******* Services requis pour effectuer des actions sur ordinateur *********

	public void envoyerMode(ModeOrdinateur mo) throws Exception {
		this.ordinateur_OUTPORT.envoyerMode(mo);	
	}
	
	// ******* Services requis pour recuperer les informations du compteur *********
	
	public void getConsommationGlobale() throws Exception {
		this.compteur_OUTPORT.getConsommationGlobale();
	}

	public void getProductionGlobale() throws Exception {
		this.compteur_OUTPORT.getProductionGlobale();
	}
	
	// ******* Service offert pour les appareils *********

	public void ajouterAppareil(String uri) throws Exception {
		this.appareils.add(uri);
	}
	
	// ******* Service offert pour les unites de production  *********

	public void ajouterUniteProduction(String uri) throws Exception {
		this.unitesProduction.add(uri);
	}
	
	/**
	 * Gerer et afficher ce qui se passe pendant l'execution du controleur
	 */
	public void runningAndPrint() {
		this.logMessage("Decisions controleur...");
		
		/** TODO code pour gerer les decisions du controleur */
	}
	
	// ************* Cycle de vie du composant ************* 

	@Override
	public void start() throws ComponentStartException {
		super.start();
		
		this.logMessage("Demarrage du controleur...");

		this.scheduleTask(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try { ((Controleur) this.getTaskOwner()).envoyerEtatPanneauSolaire(EtatUniteProduction.ON); }
				catch (Exception e) { throw new RuntimeException(e); }
			}
		}, 1000, TimeUnit.MILLISECONDS);
		
		this.scheduleTask(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try { ((Controleur) this.getTaskOwner()).envoyerEtatBatterie(EtatUniteProduction.ON); }
				catch (Exception e) { throw new RuntimeException(e); }
			}
		}, 1000, TimeUnit.MILLISECONDS);
		
		this.scheduleTask(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try { ((Controleur) this.getTaskOwner()).envoyerEtatFrigo(EtatAppareil.ON); }
				catch (Exception e) { throw new RuntimeException(e); }
			}
		}, 2000, TimeUnit.MILLISECONDS);
		
		this.scheduleTask(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try { ((Controleur) this.getTaskOwner()).envoyerEtatOrdinateur(EtatAppareil.ON); }
				catch (Exception e) { throw new RuntimeException(e); }
			}
		}, 2000, TimeUnit.MILLISECONDS);
	}

	@Override
	public void execute() throws Exception {
		super.execute();
		
		this.logMessage("Phase d'execution du controleur.");
		
		this.logMessage("Execution en cours...");
		
		this.scheduleTaskWithFixedDelay(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try { ((Controleur) this.getTaskOwner()).runningAndPrint(); } 
				catch (Exception e) { throw new RuntimeException(e); }
			}
		}, 4000, 1000, TimeUnit.MILLISECONDS);
	}
	
	@Override
	public void finalise() throws Exception {
		this.logMessage("Arret du composant controleur...") ;
		super.finalise();
	}
	
	@Override
	public void	shutdown() throws ComponentShutdownException
	{
		try {
			PortI[] port_controleur = this.findPortsFromInterface(ILaveLinge.class);
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
			PortI[] port_controleur = this.findPortsFromInterface(ILaveLinge.class);
			PortI[] port_consommation = this.findPortsFromInterface(IConsommation.class);
			PortI[] port_ajoutappareil = this.findPortsFromInterface(IAjoutAppareil.class);
			
			port_controleur[0].unpublishPort() ;
			port_consommation[0].unpublishPort();
			port_ajoutappareil[0].unpublishPort();
		} catch (Exception e) { throw new ComponentShutdownException(e); }
		super.shutdownNow();
	}
}
