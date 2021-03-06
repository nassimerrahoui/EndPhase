package app.components;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import app.CVM;
import app.interfaces.assembleur.IComposantDynamique;
import app.interfaces.compteur.ICompteur;
import app.interfaces.compteur.ICompteurControleur;
import app.ports.compteur.CompteurAssembleurInPort;
import app.ports.compteur.CompteurInPort;
import app.ports.compteur.ConsommationProductionInPort;
import app.util.URI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.ports.PortI;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.SimulationEngineCreationMode;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import simulator.models.compteur.CompteurModel;
import simulator.plugins.CompteurSimulatorPlugin;

/**
 * @author Willy Nassim
 */

@OfferedInterfaces(offered = { ICompteurControleur.class, ICompteur.class, IComposantDynamique.class })
@RequiredInterfaces(required = { })
public class Compteur extends AbstractCyPhyComponent implements EmbeddingComponentAccessI {

	/** map des appareils et leur consommation 
	 *  seulement utilise pour l'etape 1 du projet */
	protected ConcurrentHashMap<String, Double> appareil_consommation = new ConcurrentHashMap<>();
	
	/** map des unites de production et leur production 
	 *  seulement utilise pour l'etape 1 du projet */
	protected ConcurrentHashMap<String, Double> unite_production = new ConcurrentHashMap<>();
	
	/** Plugin pour interagir avec le modele du compteur */
	protected CompteurSimulatorPlugin asp;
	
	public static int ORIGIN_X = CVM.plotX ;
	public static int ORIGIN_Y = CVM.plotY ;
	
	/** consommation globale de tous les appareils */
	protected double consommation_globale;
	
	/** production globale de toutes les unites de production */
	protected double production_globale;

	protected Compteur(
			String COMPTEUR_URI,
			int nbThreads, int nbSchedulableThreads) throws Exception {
		super(COMPTEUR_URI, nbThreads, nbSchedulableThreads);
		
		// port entrant pour recuperer les consommation des appareils
		// et recuperer les productions des unites de production
		ConsommationProductionInPort consommation_production_INPORT = new ConsommationProductionInPort(this);
		
		// port entrant permettant au controleur de recuperer des informations depuis le compteur
		CompteurInPort action_INPORT = new CompteurInPort(this);
		
		// port entrant pour l'assembleur
		CompteurAssembleurInPort launch_port = new CompteurAssembleurInPort(this);
		
		consommation_production_INPORT.publishPort();
		action_INPORT.publishPort();
		launch_port.publishPort();
		
		if (AbstractCVM.isDistributed) {
			this.executionLog.setDirectory(System.getProperty("user.dir")) ;
		} else {
			this.executionLog.setDirectory(System.getProperty("user.home")) ;
		}
		
		this.createNewExecutorService(URI.POOL_CONSO_PROD_COMPTEUR_URI.getURI(), 5, false) ;
		this.createNewExecutorService(URI.POOL_CONTROLE_COMPTEUR_URI.getURI(), 5, false) ;
		
		consommation_globale = 0.0;
		production_globale = 0.0;
		
		// affichage
		this.tracer.setTitle("Compteur");
		this.tracer.setRelativePosition(1, 3);
		this.toggleTracing();
		
		this.initialise();
	}

	/**
	 * Permet au controleur d'ajouter des appareils au compteur
	 * @param uri
	 * @throws Exception
	 */
	public void ajouterAppareil(String uri) throws Exception {
		this.appareil_consommation.put(uri, 0.0);
		this.logMessage(uri + " a ete ajoute au compteur");
		this.logMessage("...");
	}
	
	/**
	 * Permet au controleur d'ajouter des unites de production au compteur
	 * @param uri
	 * @throws Exception
	 */
	public void ajouterUniteProduction(String uri) throws Exception {
		this.unite_production.put(uri, 0.0);
		this.logMessage(uri + " a ete ajoute au compteur");
		this.logMessage("...");
	}

	/**
	 * Envoie la consommation globale des appareils
	 * au controleur
	 * @return
	 * @throws Exception
	 */
	public double envoyerConsommationGlobale() throws Exception {
		//premier return utilise pour l'etape 1
		//return appareil_consommation.values().stream().mapToDouble(i -> i).sum();
		return consommation_globale;
	}

	/**
	 * Envoie la production totale des unites de productions
	 * au controleur
	 * @return
	 * @throws Exception
	 */
	public double envoyerProductionGlobale() throws Exception {
		//premier return utilise pour l'etape 1
		//return unite_production.values().stream().mapToDouble(i -> i).sum();
		return production_globale;
	}
	
	/**
	 * Met a jour la consommation electrique d'un appareil
	 * si nous voulous utiliser une communication par ports pour la consommation
	 *  (m�thode utilise seulement a l'etape 1 du projet)
	 * @param uri
	 * @param consommation
	 * @throws Exception
	 */
	public void setAppareilConsommation(String uri, double consommation) throws Exception {
		double c = (double) Math.round(consommation);
		if(appareil_consommation.containsKey(uri)) {
			appareil_consommation.put(uri, c);
		}
	}

	/**
	 * Met a jour la production electrique d'une unite de production
	 * si nous voulous utiliser une communication par ports pour la production
	 *  (m�thode utilise seulement a l'etape 1 du projet)
	 * @param uri
	 * @param production
	 * @throws Exception
	 */
	public void setUniteProduction(String uri, double production) throws Exception {
		double p = (double) Math.round(production);
		if(unite_production.containsKey(uri)) {
			unite_production.put(uri, p);
		}
	}
	
	// ************* Cycle de vie du composant ************* 

	@Override
	public void start() throws ComponentStartException {
		super.start();
		this.logMessage("Demarrage du compteur...");
	}
	
	/**
	 * Execute depuis l'assembleur
	 * @throws Exception
	 */
	public void dynamicExecute() throws Exception {
		Thread.sleep(10L);
		
		this.logMessage("Recuperation de la consommation/production depuis la simulation...");
		this.scheduleTaskWithFixedDelay(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try {
					((Compteur) this.getTaskOwner()).consommation_globale = (double) ((Compteur) this.getTaskOwner()).asp.getModelStateValue(CompteurModel.URI, "consommation");
					((Compteur) this.getTaskOwner()).production_globale = (double) ((Compteur) this.getTaskOwner()).asp.getModelStateValue(CompteurModel.URI, "production");					
					((Compteur) this.getTaskOwner()).logMessage("Consommation globale : " + Math.round(consommation_globale));
					((Compteur) this.getTaskOwner()).logMessage("Production globale : " + Math.round(production_globale));
					Thread.sleep(10L);
				} catch (Exception e) { e.printStackTrace(); }
			}
		}, 1000, 1000, TimeUnit.MILLISECONDS);
	}
	
	@Override
	public void finalise() throws Exception {
		this.logMessage("Arret du composant compteur...") ;
		super.finalise();
	}
	
	@Override
	public void	shutdown() throws ComponentShutdownException
	{
		try {
			PortI[] p1 = this.findPortsFromInterface(ICompteur.class);
			PortI[] p2 = this.findPortsFromInterface(ICompteurControleur.class);
			PortI[] p3 = this.findPortsFromInterface(IComposantDynamique.class);
			
			p1[0].unpublishPort();
			p2[0].unpublishPort();
			p3[0].unpublishPort();
		} catch (Exception e) { throw new ComponentShutdownException(e); }
		super.shutdown();
	}

	@Override
	public void shutdownNow() throws ComponentShutdownException
	{
		try {
			PortI[] p1 = this.findPortsFromInterface(ICompteur.class);
			PortI[] p2 = this.findPortsFromInterface(ICompteurControleur.class);
			PortI[] p3 = this.findPortsFromInterface(IComposantDynamique.class);
			
			p1[0].unpublishPort();
			p2[0].unpublishPort();
			p3[0].unpublishPort();
		} catch (Exception e) { throw new ComponentShutdownException(e); }
		super.shutdownNow();
	}
	
	
	// ******************* Simulation *************************
	
	@Override
	protected Architecture createLocalArchitecture(String modelURI) throws Exception {
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors = new HashMap<>();
		atomicModelDescriptors.put(
				CompteurModel.URI,
				AtomicModelDescriptor.create(
						CompteurModel.class,
						CompteurModel.URI,
						TimeUnit.SECONDS,
						null,
						SimulationEngineCreationMode.ATOMIC_ENGINE)) ;
		
		Architecture localArchitecture =
				new Architecture(
						CompteurModel.URI,
						atomicModelDescriptors,
						new HashMap<>(),
						TimeUnit.SECONDS) ;
		return localArchitecture ;
	}
	
	/**
	 * Installe le plugin
	 * @throws Exception
	 */
	protected void	initialise() throws Exception {
		Architecture localArchitecture = this.createLocalArchitecture(null);
		this.asp = new CompteurSimulatorPlugin();
		this.asp.setPluginURI(localArchitecture.getRootModelURI());
		this.asp.setSimulationArchitecture(localArchitecture);
		this.installPlugin(this.asp);	
	}
	
	// ************** Plotter ******************************
	
	public static int getPlotterWidth() {
		int ret = Integer.MAX_VALUE ;
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment() ;
		GraphicsDevice[] gs = ge.getScreenDevices() ;
		for (int i = 0; i < gs.length; i++) {
			DisplayMode dm = gs[i].getDisplayMode() ;
			int width = dm.getWidth() ;
			if (width < ret) {
				ret = width ;
			}
		}
		return (int) (0.25 * ret) ;
	}

	public static int getPlotterHeight() {
		int ret = Integer.MAX_VALUE ;
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment() ;
		GraphicsDevice[] gs = ge.getScreenDevices() ;
		for (int i = 0; i < gs.length; i++) {
			DisplayMode dm = gs[i].getDisplayMode() ;
			int height = dm.getHeight() ;
			if (height < ret) {
				ret = height ;
			}
		}
		return (int) (0.2 * ret) ;
	}
}