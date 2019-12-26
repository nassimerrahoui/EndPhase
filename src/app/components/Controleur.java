package app.components;

import java.util.Vector;
import java.util.concurrent.TimeUnit;
import app.interfaces.controleur.IControleBatterie;
import app.interfaces.controleur.IControleCompteur;
import app.interfaces.controleur.IControleFrigo;
import app.interfaces.controleur.IControleLaveLinge;
import app.interfaces.controleur.IControleAspirateur;
import app.interfaces.controleur.IControlePanneau;
import app.interfaces.controleur.IControleur;
import app.interfaces.generateur.IComposantDynamique;
import app.ports.controleur.ControleurAssembleurInPort;
import app.ports.controleur.ControleurBatterieOutPort;
import app.ports.controleur.ControleurCompteurOutPort;
import app.ports.controleur.ControleurFrigoOutPort;
import app.ports.controleur.ControleurInPort;
import app.ports.controleur.ControleurLaveLingeOutPort;
import app.ports.controleur.ControleurAspirateurOutPort;
import app.ports.controleur.ControleurPanneauOutPort;
import app.util.EtatUniteProduction;
import app.util.ModeFrigo;
import app.util.ModeLaveLinge;
import app.util.ModeAspirateur;
import app.util.TemperatureLaveLinge;
import app.util.URI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.ports.PortI;

@OfferedInterfaces(offered = { IControleur.class, IComposantDynamique.class })
@RequiredInterfaces(required = { 
		IControleCompteur.class, 
		IControleFrigo.class, 
		IControleLaveLinge.class, 
		IControleAspirateur.class,
		IControlePanneau.class,
		IControleBatterie.class })
public class Controleur extends AbstractComponent {
	
	protected ControleurFrigoOutPort frigo_OUTPORT;
	protected ControleurLaveLingeOutPort lavelinge_OUTPORT;
	protected ControleurAspirateurOutPort aspirateur_OUTPORT;
	protected ControleurPanneauOutPort panneausolaire_OUTPORT;
	protected ControleurBatterieOutPort batterie_OUTPORT;
	protected ControleurCompteurOutPort compteur_OUTPORT;

	protected Vector<String> unitesProduction = new Vector<>();
	protected Vector<String> appareils = new Vector<>();

	protected Controleur(
			String CONTROLEUR_URI,
			String CONTROLEUR_OP_FRIGO_URI, 
			String CONTROLEUR_OP_LAVELINGE_URI,
			String CONTROLEUR_OP_ASPIRATEUR_URI,
			String CONTROLEUR_OP_PANNEAUSOLAIRE_URI,
			String CONTROLEUR_OP_BATTERIE_URI,
			String CONTROLEUR_OP_COMPTEUR_URI,
			int nbThreads, int nbSchedulableThreads) throws Exception {
		super(CONTROLEUR_URI, nbThreads, nbSchedulableThreads);
		
		frigo_OUTPORT = new ControleurFrigoOutPort(CONTROLEUR_OP_FRIGO_URI,this);
		lavelinge_OUTPORT = new ControleurLaveLingeOutPort(CONTROLEUR_OP_LAVELINGE_URI,this);
		aspirateur_OUTPORT = new ControleurAspirateurOutPort(CONTROLEUR_OP_ASPIRATEUR_URI,this);
		panneausolaire_OUTPORT = new ControleurPanneauOutPort(CONTROLEUR_OP_PANNEAUSOLAIRE_URI,this);
		batterie_OUTPORT = new ControleurBatterieOutPort(CONTROLEUR_OP_BATTERIE_URI,this);
		compteur_OUTPORT = new ControleurCompteurOutPort(CONTROLEUR_OP_COMPTEUR_URI,this);
		
		// port entrant permettant aux appareils et unites de production de s'inscrire
		ControleurInPort inscription_INPORT = new ControleurInPort(this);
		
		// port pour l'assembleur
		ControleurAssembleurInPort launch_INPORT = new ControleurAssembleurInPort(this);
		
		frigo_OUTPORT.publishPort();
		lavelinge_OUTPORT.publishPort();
		aspirateur_OUTPORT.publishPort();
		panneausolaire_OUTPORT.publishPort();
		batterie_OUTPORT.publishPort();
		compteur_OUTPORT.publishPort();
		inscription_INPORT.publishPort();
		launch_INPORT.publishPort();

		if (AbstractCVM.isDistributed) {
			this.executionLog.setDirectory(System.getProperty("user.dir")) ;
		} else {
			this.executionLog.setDirectory(System.getProperty("user.home")) ;
		}
		
		/** TODO definir des constantes pour les pools */
		this.createNewExecutorService(URI.POOL_AJOUT_CONTROLEUR_URI.getURI(), 5, false) ;
		this.createNewExecutorService(URI.POOL_CONSO_PROD_CONTROLEUR_URI.getURI(), 5, false) ;
		
		// affichage
		this.tracer.setTitle("Controleur");
		this.tracer.setRelativePosition(1, 0);
	}
	
	// ******* Services requis pour changer le mode des appareils *********

	public void envoyerEtatFrigo(ModeFrigo etat) throws Exception {
		this.frigo_OUTPORT.envoyerModeFrigo(etat);
	}
	
	public void envoyerEtatLaveLinge(ModeLaveLinge etat) throws Exception {
		this.lavelinge_OUTPORT.envoyerModeLaveLinge(etat);
	}
	
	public void envoyerEtatAspirateur(ModeAspirateur etat) throws Exception {
		this.aspirateur_OUTPORT.envoyerModeAspirateur(etat);
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
		this.compteur_OUTPORT.demanderAjoutAppareil(uri);
	}
	
	// ******* Service offert pour les unites de production  *********

	public void ajouterUniteProduction(String uri) throws Exception {
		this.unitesProduction.add(uri);
		this.compteur_OUTPORT.demanderAjoutUniteProduction(uri);
	}
	
	/**
	 * Gerer et afficher ce qui se passe pendant l'execution du controleur
	 * @throws Exception 
	 */
	public void runningAndPrint() throws Exception {
		this.logMessage("Decisions controleur...");
		
		/** TODO code pour gerer les decisions reactives du controleur */
		
		// TEST
		int i = 0;
		if(i == 0) {
			envoyerTemperature_Refrigerateur(4.0);
			i++;
		}
		
		this.logMessage("...");
	}
	
	// ************* Cycle de vie du composant ************* 

	@Override
	public void start() throws ComponentStartException {
		super.start();
		this.logMessage("Demarrage du controleur...");
	}
	
	public void dynamicExecute() throws Exception {
		
		this.logMessage("Phase d'execution du controleur.");
		
		this.logMessage("Execution en cours...");
		
		/** TODO Traitement global deliberatif ??? 
		 * en utilisant des synthese (historique, donnees)
		 * donnees -> etat -> planification
		 * */
		
		this.scheduleTaskWithFixedDelay(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try { ((Controleur) this.getTaskOwner()).runningAndPrint(); } 
				catch (Exception e) { throw new RuntimeException(e); }
			}
		}, 4000, 5000, TimeUnit.MILLISECONDS);
		
		this.scheduleTask(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try { ((Controleur) this.getTaskOwner()).envoyerEtatPanneauSolaire(EtatUniteProduction.ON); }
				catch (Exception e) { throw new RuntimeException(e); }
			}
		}, 3000, TimeUnit.MILLISECONDS);
		
		this.scheduleTask(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try { ((Controleur) this.getTaskOwner()).envoyerEtatBatterie(EtatUniteProduction.ON); }
				catch (Exception e) { throw new RuntimeException(e); }
			}
		}, 3000, TimeUnit.MILLISECONDS);
		
		this.scheduleTask(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try { ((Controleur) this.getTaskOwner()).envoyerEtatFrigo(ModeFrigo.LIGHT_OFF); }
				catch (Exception e) { throw new RuntimeException(e); }
			}
		}, 3000, TimeUnit.MILLISECONDS);
		
		this.scheduleTask(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try { ((Controleur) this.getTaskOwner()).envoyerEtatAspirateur(ModeAspirateur.PERFORMANCE_REDUITE); }
				catch (Exception e) { throw new RuntimeException(e); }
			}
		}, 3000, TimeUnit.MILLISECONDS);
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
			PortI[] p1 = this.findPortsFromInterface(IControleur.class);
			PortI[] p2 = this.findPortsFromInterface(IControleFrigo.class);
			PortI[] p3 = this.findPortsFromInterface(IControleLaveLinge.class);
			PortI[] p4 = this.findPortsFromInterface(IControleAspirateur.class);
			PortI[] p5 = this.findPortsFromInterface(IControlePanneau.class);
			PortI[] p6 = this.findPortsFromInterface(IControleBatterie.class);
			PortI[] p7 = this.findPortsFromInterface(IControleCompteur.class);
			PortI[] p8 = this.findPortsFromInterface(IComposantDynamique.class);
			
			p1[0].unpublishPort();
			p2[0].unpublishPort();
			p3[0].unpublishPort();
			p4[0].unpublishPort();
			p5[0].unpublishPort();
			p6[0].unpublishPort();
			p7[0].unpublishPort();
			p8[0].unpublishPort();
			
		} catch (Exception e) { throw new ComponentShutdownException(e); }
		super.shutdown();
	}

	@Override
	public void shutdownNow() throws ComponentShutdownException
	{
		try {
			PortI[] p1 = this.findPortsFromInterface(IControleur.class);
			PortI[] p2 = this.findPortsFromInterface(IControleFrigo.class);
			PortI[] p3 = this.findPortsFromInterface(IControleLaveLinge.class);
			PortI[] p4 = this.findPortsFromInterface(IControleAspirateur.class);
			PortI[] p5 = this.findPortsFromInterface(IControlePanneau.class);
			PortI[] p6 = this.findPortsFromInterface(IControleBatterie.class);
			PortI[] p7 = this.findPortsFromInterface(IControleCompteur.class);
			PortI[] p8 = this.findPortsFromInterface(IComposantDynamique.class);
			
			p1[0].unpublishPort();
			p2[0].unpublishPort();
			p3[0].unpublishPort();
			p4[0].unpublishPort();
			p5[0].unpublishPort();
			p6[0].unpublishPort();
			p7[0].unpublishPort();
			p8[0].unpublishPort();
			
		} catch (Exception e) { throw new ComponentShutdownException(e); }
		super.shutdownNow();
	}
}
