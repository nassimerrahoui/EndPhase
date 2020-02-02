package app.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import app.interfaces.controleur.IControleBatterie;
import app.interfaces.controleur.IControleCompteur;
import app.interfaces.controleur.IControleFrigo;
import app.interfaces.controleur.IControleLaveLinge;
import app.interfaces.assembleur.IComposantDynamique;
import app.interfaces.controleur.IControleAspirateur;
import app.interfaces.controleur.IControlePanneau;
import app.interfaces.controleur.IControleur;
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
import app.util.TypeAppareil;
import app.util.URI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.ports.PortI;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.SimulationEngineCreationMode;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import simulator.models.controleur.ControleurModel;
import simulator.models.controleur.OrderManagerComponentAccessI;
import simulator.plugins.ControleurSimulatorPlugin;

@OfferedInterfaces(offered = { IControleur.class, IComposantDynamique.class })
@RequiredInterfaces(required = { 
		IControleCompteur.class, 
		IControleFrigo.class, 
		IControleLaveLinge.class, 
		IControleAspirateur.class,
		IControlePanneau.class,
		IControleBatterie.class })

/**
 * Ce composant effectue differentes actions sur l'ensemble des appareils/unites de productions.
 * @author Willy Nassim
 *
 */
public class Controleur extends AbstractCyPhyComponent implements OrderManagerComponentAccessI{
	
	protected ControleurFrigoOutPort frigo_OUTPORT;
	protected ControleurLaveLingeOutPort lavelinge_OUTPORT;
	protected ControleurAspirateurOutPort aspirateur_OUTPORT;
	protected ControleurPanneauOutPort panneausolaire_OUTPORT;
	protected ControleurBatterieOutPort batterie_OUTPORT;
	protected ControleurCompteurOutPort compteur_OUTPORT;

	/** Liste des unites de productions du systeme */
	protected Vector<String> unitesProduction = new Vector<>();
	
	/** Map contenant l'indice de priorite pour chaque appareil 
	 *  uri -> priorite */
	protected HashMap<String, TypeAppareil> appareils_priority = new HashMap<>();
	
	/** Map contenant la classe d'appareil pour chaque appareil
	 *  uri -> class appareil */
	protected HashMap<String, String> appareils_className = new HashMap<>();
	
	/**
	 * En fonction de ce niveau, le controleur effectue des actions de regulations
	 * afin de stabiliser le rapport production/consommation
	 * Son but est de toujours garder une consommation inferieure ou egale a la production
	 */
	protected int niveauDeControle = 1;
	
	/** Plugin pour interagir avec le model du controleur */
	protected ControleurSimulatorPlugin	asp ;

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
		
		this.createNewExecutorService(URI.POOL_AJOUT_CONTROLEUR_URI.getURI(), 5, false) ;
		this.createNewExecutorService(URI.POOL_CONSO_PROD_CONTROLEUR_URI.getURI(), 5, false) ;
		
		// affichage
		this.tracer.setTitle("Controleur");
		this.tracer.setRelativePosition(2, 3);
		
		this.initialise();
	}
	
	// ******* Services requis pour changer le mode des appareils *********

	/**
	 * Ordonne au frigo de se mettre dans un etat
	 * @param etat
	 * @throws Exception
	 */
	public void envoyerEtatFrigo(ModeFrigo etat) throws Exception {
		this.frigo_OUTPORT.envoyerModeFrigo(etat);
	}
	
	/**
	 * Ordonne au lave-linge de se mettre dans un etat
	 * @param etat
	 * @throws Exception
	 */
	public void envoyerEtatLaveLinge(ModeLaveLinge etat) throws Exception {
		this.lavelinge_OUTPORT.envoyerModeLaveLinge(etat);
	}
	
	/**
	 * Ordonne a l'aspirateur de se mettre dans un etat
	 * @param etat
	 * @throws Exception
	 */
	public void envoyerEtatAspirateur(ModeAspirateur etat) throws Exception {
		this.aspirateur_OUTPORT.envoyerModeAspirateur(etat);
	}

	// ******* Services requis pour allumer ou eteindre des unites de production *********
	
	/**
	 * Ordonne au panneau solaire de se mettre dans un etat
	 * @param etat
	 * @throws Exception
	 */
	public void envoyerEtatPanneauSolaire(EtatUniteProduction etat) throws Exception {
		this.panneausolaire_OUTPORT.envoyerEtatUniteProduction(etat);	
	}
	
	/**
	 * Ordonne a la batterie de se mettre dans un etat
	 * @param etat
	 * @throws Exception
	 */
	public void envoyerEtatBatterie(EtatUniteProduction etat) throws Exception {
		this.batterie_OUTPORT.envoyerEtatUniteProduction(etat);	
	}

	// ******* Services requis pour effectuer des actions sur lave-linge *********
	
	/**
	 * Envoie une planification de taches au lave-linge
	 * @param planification
	 * @param heure
	 * @param minutes
	 * @throws Exception
	 */
	public void envoyerPlanificationCycle(ArrayList<ModeLaveLinge> planification, int heure, int minutes) throws Exception {
		this.lavelinge_OUTPORT.envoyerPlanificationCycle(planification, heure, minutes);		
	}

	/**
	 * Modifie la temperature de l'eau du lave-linge
	 * @param tl
	 * @throws Exception
	 */
	public void envoyerTemperature(TemperatureLaveLinge tl) throws Exception {
		this.lavelinge_OUTPORT.envoyerTemperature(tl);
	}
	
	// ******* Services requis pour effectuer des actions sur frigo *********

	/**
	 * Modifie la temperature du refrigerateur
	 * @param temperature
	 * @throws Exception
	 */
	public void envoyerTemperature_Refrigerateur(double temperature) throws Exception {
		this.frigo_OUTPORT.envoyerTemperature_Refrigerateur(temperature);
	}

	/**
	 * Modifie la temperature du congelateur
	 * @param temperature
	 * @throws Exception
	 */
	public void envoyerTemperature_Congelateur(double temperature) throws Exception {
		this.frigo_OUTPORT.envoyerTemperature_Congelateur(temperature);
	}
	
	// ******* Services requis pour recuperer les informations du compteur *********
	
	/**
	 * Recupere la consommation globale
	 * @return
	 * @throws Exception
	 */
	public double getConsommationGlobale() throws Exception {
		return this.compteur_OUTPORT.getConsommationGlobale();
	}

	/**
	 * Recupere la production globale
	 * @return
	 * @throws Exception
	 */
	public double getProductionGlobale() throws Exception {
		return this.compteur_OUTPORT.getProductionGlobale();
	}
	
	// ******* Service offert pour les appareils *********

	/**
	 * Permet aux appareils de demander un ajout au controleur
	 * @param uri
	 * @param className
	 * @param type
	 * @throws Exception
	 */
	public void ajouterAppareil(String uri, String className, TypeAppareil type) throws Exception {
		assert className != null;
		assert type != null;
		
		this.appareils_priority.put(uri, type);
		this.appareils_className.put(uri, className);
		this.compteur_OUTPORT.demanderAjoutAppareil(uri);
	}
	
	// ******* Service offert pour les unites de production  *********

	/**
	 * Permet aux unites de production de demander un ajout au controleur
	 * @param uri
	 * @throws Exception
	 */
	public void ajouterUniteProduction(String uri) throws Exception {
		this.unitesProduction.add(uri);
		this.compteur_OUTPORT.demanderAjoutUniteProduction(uri);
	}
	
	@Override
	public void controlTask(double simulatedTime) throws Exception {
		runningAndPrint();
	}
	
	/**
	 * Les decisions du controleur
	 * Gere et affiche ce qui se passe pendant l'execution du controleur
	 * 
	 * Si la consommation electrique est plus elevee que la production electrique,
	 * le controleur va chercher a eteindre les appareils selon une priorite definie
	 * par le type des appareils
	 * 
	 * @throws Exception 
	 */
	public void runningAndPrint() throws Exception {
		
		double consommation = getConsommationGlobale();
		double production = getProductionGlobale();
		
		if(consommation > production) {
			ArrayList<String> uris = new ArrayList<>();
			
			switch(niveauDeControle) {
				case 1 :
					for(String uri : appareils_priority.keySet()) {
						if(appareils_priority.get(uri).getValue() == 3)
							uris.add(uri);
					}
					
					for(String uri : uris) {
						if (appareils_className.get(uri).equals(Aspirateur.class.getName())) {
							this.runTask(new AbstractComponent.AbstractTask() {
								@Override
								public void run() {
									try {
										((Controleur) this.getTaskOwner()).envoyerEtatAspirateur(ModeAspirateur.OFF); 
									} catch (Exception e) { throw new RuntimeException(e); }
								}
							});
						}
					}
					this.logMessage("Extinction de l'aspirateur...");
					break;
					
				case 2 :
					for(String uri : appareils_priority.keySet()) {
						if(appareils_priority.get(uri).getValue() == 2)
							uris.add(uri);
					}
					
					for(String uri : uris) {
						if (appareils_className.get(uri).equals(LaveLinge.class.getName())) {
							this.runTask(new AbstractComponent.AbstractTask() {
								@Override
								public void run() {
									try {
																		
											((Controleur) this.getTaskOwner()).envoyerEtatLaveLinge(ModeLaveLinge.OFF); 
									} catch (Exception e) { throw new RuntimeException(e); }
								}
							});
						}
					}
					this.logMessage("Extinction du lave-linge...");
					break;
					
				case 3 :
					this.runTask(new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								((Controleur) this.getTaskOwner()).envoyerEtatBatterie(EtatUniteProduction.ON);
							} catch (Exception e) { throw new RuntimeException(e); }
						}
					});
					this.logMessage("Allumage de la batterie...");
					break;
			}
			
			if(niveauDeControle <= 3) {
				niveauDeControle++;
				this.logMessage("Stabilisation...");
			}

		} else {
			niveauDeControle = 1;
		}
	}
	
	// ************* Cycle de vie du composant ************* 

	@Override
	public void start() throws ComponentStartException {
		super.start();
		this.logMessage("Demarrage du controleur...");
	}
	
	/**
	 * Execute depuis l'aseembleur
	 * @throws Exception
	 */
	public void dynamicExecute() throws Exception {
		this.logMessage("Début scénario...");
		
		this.logMessage("Allumge du panneau solaire");
		this.runTask(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try { ((Controleur) this.getTaskOwner()).envoyerEtatPanneauSolaire(EtatUniteProduction.ON); }
				catch (Exception e) { throw new RuntimeException(e); }
			}
		});
		
		this.logMessage("Allumge du frigo");
		this.scheduleTask(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try { ((Controleur) this.getTaskOwner()).envoyerEtatFrigo(ModeFrigo.LIGHT_OFF); }
				catch (Exception e) { throw new RuntimeException(e); }
			}
		}, 4000, TimeUnit.MILLISECONDS);
		
		this.logMessage("Allumge de l'aspirateur");
		this.scheduleTask(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try { ((Controleur) this.getTaskOwner()).envoyerEtatAspirateur(ModeAspirateur.PERFORMANCE_REDUITE); }
				catch (Exception e) { throw new RuntimeException(e); }
			}
		}, 6000, TimeUnit.MILLISECONDS);
		
		this.logMessage("Reglage temperature cible pour le refrigerateur");
		this.scheduleTask(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try { ((Controleur) this.getTaskOwner()).envoyerTemperature_Refrigerateur(3.5); }
				catch (Exception e) { throw new RuntimeException(e); }
			}
		}, 6000, TimeUnit.MILLISECONDS);
		
		this.logMessage("Planification du lave-ling");
		this.scheduleTaskWithFixedDelay(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try {
					
					ArrayList<ModeLaveLinge> p = new ArrayList<>();
					p.add(ModeLaveLinge.LAVAGE);
					p.add(ModeLaveLinge.RINCAGE);
					p.add(ModeLaveLinge.ESSORAGE);
					p.add(ModeLaveLinge.SECHAGE);
					p.add(ModeLaveLinge.VEILLE);
					((Controleur) this.getTaskOwner()).envoyerPlanificationCycle(p, 0, 3);
				}
				catch (Exception e) { throw new RuntimeException(e); }
			}
		}, 8000, 20000, TimeUnit.MILLISECONDS);
		
		HashMap<String, Object> simParams = new HashMap<String, Object>();
		simParams.put(ControleurModel.URI + " : " + ControleurModel.COMPONENT_REF, this);
		
		this.asp.setSimulationRunParameters(simParams);
		
		this.asp.doStandAloneSimulation(0.0, 60000.0) ;
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

	/**
	 * Installe le plugin
	 * @throws Exception
	 */
	protected void initialise() throws Exception {
		Architecture localArchitecture = this.createLocalArchitecture(null);
		this.asp = new ControleurSimulatorPlugin();
		this.asp.setPluginURI(localArchitecture.getRootModelURI());
		this.asp.setSimulationArchitecture(localArchitecture);
		this.installPlugin(this.asp);
	}
	
	@Override
	protected Architecture createLocalArchitecture(String modelURI) throws Exception {
		
		Map<String, AbstractAtomicModelDescriptor> atomicModelDescriptors = new HashMap<>();

		atomicModelDescriptors.put(ControleurModel.URI, AtomicModelDescriptor.create(ControleurModel.class,
				ControleurModel.URI, TimeUnit.SECONDS, null, SimulationEngineCreationMode.ATOMIC_ENGINE));
		Architecture localArchitecture = new Architecture(ControleurModel.URI, atomicModelDescriptors, new HashMap<>(),
				TimeUnit.SECONDS);
		return localArchitecture;

		
	}
	
}
