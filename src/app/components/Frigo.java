package app.components;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.concurrent.TimeUnit;
import app.CVM;
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
import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.ports.PortI;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import simulator.models.frigo.FrigoCoupledModel;
import simulator.models.frigo.FrigoModel;
import simulator.plugins.FrigoSimulatorPlugin;

/**
 * @author Willy Nassim
 */

@OfferedInterfaces(offered = { IFrigo.class, IComposantDynamique.class })
@RequiredInterfaces(required = { IAjoutAppareil.class, IConsommation.class })
public class Frigo	
	extends AbstractCyPhyComponent 
	implements EmbeddingComponentAccessI {
	
	/** port sortant permettant a l'appareil de s'inscrire sur la liste des appareil du controleur */
	protected FrigoControleurOutPort controleur_OUTPORT;
	
	/** port sortant permettant au compteur de recupere la consommation du frigo */
	protected FrigoCompteurOutPort consommation_OUTPORT;

	/** Gestion de priorite pour les decisions du controleur*/
	protected TypeAppareil type;
	
	/** Etat actuel de l'appareil */
	protected ModeFrigo etat;

	/** temperature actuelle du refrigerateur */
	protected double  refrigerateur_current_temperature;
	
	/** temperature actuelle du congelateur (non implemente actuellement) */
	protected double  congelateur_current_temperature;
	
	/** temperature cible de refrigerateur */
	protected double refrigerateur_temperature_cible;
	
	/** temperature cible de congelateur (non implemente actuellement) */
	protected double congelateur_temperature_cible;

	/** Consommation en Watts par l'appareil */
	protected double consommation;
	
	/** Plugin pour interagir avec le model du simulator */
	protected FrigoSimulatorPlugin asp;
	
	public static int ORIGIN_X = CVM.plotX ;
	public static int ORIGIN_Y = CVM.plotY ;

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
		
		// port entrant permettant a l'assembleur de deployer le composant
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
		this.tracer.setRelativePosition(2, 1);

		// attributs
		this.type = type;
		this.etat = ModeFrigo.OFF;
		this.refrigerateur_temperature_cible = 3.0;
		this.congelateur_temperature_cible = -10.0;
		this.consommation = 55.0;
		this.refrigerateur_current_temperature = FrigoModel.AMBIENT_TEMPERATURE;
		
		this.initialise();
	}

	/**
	 * Ajoute l'URI de l'appareil a la map des appareils du controleur
	 * @param uri
	 * @throws Exception
	 */
	public void demandeAjoutControleur(String uri) throws Exception {
		this.controleur_OUTPORT.demandeAjoutControleur(uri, getClass().getName(), this.type);
	}

	/**
	 * Envoie la consommation au compteur
	 * @param uri
	 * @param consommation
	 * @throws Exception
	 */
	public void envoyerConsommation(String uri, double consommation) throws Exception {
		this.consommation_OUTPORT.envoyerConsommation(uri, consommation);
	}

	/**
	 * Modifie l'etat du frigo
	 * @param etat
	 * @throws Exception
	 */
	public void setModeFrigo(ModeFrigo etat) throws Exception {
		this.etat = etat;
	}

	/**
	 * Modifie la temperature du refrigerateur
	 * @param temperature
	 * @throws Exception
	 */
	public void setTemperature_Refrigerateur(double temperature) throws Exception {
		this.refrigerateur_temperature_cible = temperature;
	}

	/**
	 * Modifie la temperature du congelateur
	 * @param temperature
	 * @throws Exception
	 */
	public void setTemperature_Congelateur(double temperature) throws Exception {
		this.congelateur_temperature_cible = temperature;
	}

	/** Actions du frigo pendant l'execution */
	protected void runningAndPrint() {
		// unused
	}
	
	// ************* Cycle de vie du composant ************* 

	@Override
	public void start() throws ComponentStartException {
		super.start();
		this.logMessage("Demarrage du frigo...");
	}
	
	/**
	 * Execution depuis l'assembleur
	 * @throws Exception
	 */
	public void dynamicExecute() throws Exception {
		this.logMessage("Phase d'execution du frigo.");
		
		Thread.sleep(10L);
		this.logMessage("Recuperation de la consommation depuis le modele...");
		this.scheduleTaskWithFixedDelay(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try {
					((Frigo) this.getTaskOwner()).consommation = (double) ((Frigo) this.getTaskOwner()).asp.getModelStateValue(FrigoModel.URI, "consommation");
					((Frigo) this.getTaskOwner()).refrigerateur_current_temperature = (double) ((Frigo) this.getTaskOwner()).asp.getModelStateValue(FrigoModel.URI, "temperature");
					((Frigo) this.getTaskOwner()).logMessage("Mode : " + etat);
					((Frigo) this.getTaskOwner()).logMessage("Consommation : " + Math.round(consommation));
					Thread.sleep(10L);
				} catch (Exception e) { e.printStackTrace(); }
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
		return FrigoCoupledModel.build();
	}

	@Override
	public Object getEmbeddingComponentStateValue(String name) throws Exception {
		if(name.equals(FrigoModel.URI + " : state")) {
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
	
	
	/**
	 * Installe le plugin
	 * @throws Exception
	 */
	protected void initialise() throws Exception {
		
		Architecture localArchitecture = this.createLocalArchitecture(null) ;
		this.asp = new FrigoSimulatorPlugin();
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
