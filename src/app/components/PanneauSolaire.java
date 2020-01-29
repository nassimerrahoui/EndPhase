package app.components;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import app.CVM;
import app.interfaces.assembleur.IComposantDynamique;
import app.interfaces.production.IAjoutUniteProduction;
import app.interfaces.production.IPanneau;
import app.interfaces.production.IProduction;
import app.ports.panneausolaire.PanneauAssembleurInPort;
import app.ports.panneausolaire.PanneauCompteurOutPort;
import app.ports.panneausolaire.PanneauControleurOutPort;
import app.ports.panneausolaire.PanneauInPort;
import app.util.EtatUniteProduction;
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
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import fr.sorbonne_u.utils.PlotterDescription;
import simulator.models.panneausolaire.PanneauSolaireCoupledModel;
import simulator.models.panneausolaire.PanneauSolaireModel;
import simulator.plugins.PanneauSolaireSimulatorPlugin;

@OfferedInterfaces(offered = { IPanneau.class, IComposantDynamique.class })
@RequiredInterfaces(required = { IAjoutUniteProduction.class, IProduction.class })
public class PanneauSolaire 
	extends AbstractCyPhyComponent 
	implements EmbeddingComponentAccessI {

	/** port sortant permettant a l'unite de s'inscrire sur la liste des unites du controleur */
	protected PanneauControleurOutPort controleur_OUTPORT;
	
	/** port sortant permettant au compteur de recupere la production de l'unite */
	protected PanneauCompteurOutPort production_OUTPORT;
	
	protected EtatUniteProduction etat;
	protected double production;
	
	protected PanneauSolaireSimulatorPlugin asp;
	
	public static int ORIGIN_X = CVM.plotX;
	public static int ORIGIN_Y = CVM.plotY;

	protected PanneauSolaire(
			String PANNEAUSOLAIRE_URI, 
			String PANNEAUSOLAIRE_COMPTEUR_OP_URI,
			String PANNEAUSOLAIRE_CONTROLEUR_OP_URI,
			int nbThreads, int nbSchedulableThreads) throws Exception {
		super(PANNEAUSOLAIRE_URI, nbThreads, nbSchedulableThreads);

		this.controleur_OUTPORT = new PanneauControleurOutPort(PANNEAUSOLAIRE_CONTROLEUR_OP_URI,this);
		this.production_OUTPORT = new PanneauCompteurOutPort(PANNEAUSOLAIRE_COMPTEUR_OP_URI,this);
		
		// port entrant permettant au controleur d'effectuer des actions sur le panneau solaire
		PanneauInPort action_INPORT = new PanneauInPort(this);
		
		// port entrant permettant a l'assembleur d'effectuer d'integrer l'entite au logement
		PanneauAssembleurInPort launch_INPORT = new PanneauAssembleurInPort(this);
		
		controleur_OUTPORT.publishPort();
		production_OUTPORT.publishPort();
		action_INPORT.publishPort();
		launch_INPORT.publishPort();
		
		if (AbstractCVM.isDistributed) {
			this.executionLog.setDirectory(System.getProperty("user.dir")) ;
		} else {
			this.executionLog.setDirectory(System.getProperty("user.home")) ;
		}
		
		this.createNewExecutorService(URI.POOL_ACTION_PANNEAUSOLAIRE_URI.getURI(), 5, false) ;
		
		// affichage
		this.tracer.setTitle("Panneau Solaire");
		this.tracer.setRelativePosition(1, 2);

		// attributs
		etat = EtatUniteProduction.OFF;
		production = 0.0;
		
		this.initialise();
	}
	

	public void demandeAjoutControleur(String uri) throws Exception {
		this.controleur_OUTPORT.demandeAjoutControleur(uri);
	}

	public void envoyerProduction(String uri, double production) throws Exception {
		this.production_OUTPORT.envoyerProduction(uri, production);
	}
	
	public void setEtatUniteProduction(EtatUniteProduction etat) throws Exception {
		this.etat = etat;
	}
	
	// ************* Cycle de vie du composant ************* 

	@Override
	public void start() throws ComponentStartException {
		super.start();
		this.logMessage("Demarrage du panneau solaire...");
	}
	
	public void dynamicExecute() throws Exception {
		
		this.logMessage("Phase d'execution du panneau solaire.");
		
		this.logMessage("Execution en cours...");
		
		this.scheduleTaskWithFixedDelay(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try { ((PanneauSolaire) this.getTaskOwner()).envoyerProduction(URI.PANNEAUSOLAIRE_URI.getURI(), production); } 
				catch (Exception e) { throw new RuntimeException(e); }
			}
		}, 2000, 1000, TimeUnit.MILLISECONDS);
		
		execute();
	}
	
	@Override
	public void execute() throws Exception {
		SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 10L ;

		HashMap<String,Object> simParams = new HashMap<String,Object>() ;
		simParams.put(PanneauSolaireModel.URI + " : " + PanneauSolaireModel.COMPONENT_REF, this);
		
		simParams.put(PanneauSolaireModel.URI + " : " + PanneauSolaireModel.INTENSITY_PLOTTING_PARAM_NAME, new PlotterDescription(
				"Ensoleilement Panneau Solaire", 
				"Temps (sec)", 
				"Rayonnement (KWC)", 
				ORIGIN_X + 2 * getPlotterWidth(),
		  		ORIGIN_Y + 2 * getPlotterHeight(),
		  		getPlotterWidth(),
		  		getPlotterHeight())) ;
		
		this.asp.setSimulationRunParameters(simParams) ;

		this.runTask(
				new AbstractComponent.AbstractTask() {
					@Override
					public void run() {
						try {
							asp.doStandAloneSimulation(0.0, 60000.0) ;
						} catch (Exception e) {
							throw new RuntimeException(e) ;
						}
					}
				});
		
		Thread.sleep(10L);
		
		this.scheduleTaskWithFixedDelay(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try {
					((PanneauSolaire) this.getTaskOwner()).etat = (EtatUniteProduction) ((PanneauSolaire) this.getTaskOwner()).asp.getModelStateValue(PanneauSolaireModel.URI, "state");
					((PanneauSolaire) this.getTaskOwner()).production = (Double) ((PanneauSolaire) this.getTaskOwner()).asp.getModelStateValue(PanneauSolaireModel.URI, "energy");
					((PanneauSolaire) this.getTaskOwner()).logMessage("Mode : " + etat);
					((PanneauSolaire) this.getTaskOwner()).logMessage("Production : " + production);
				} catch (Exception e) { e.printStackTrace(); }
			}
		}, 4000, 1000, TimeUnit.MILLISECONDS);
	}

	@Override
	public void finalise() throws Exception {
		this.logMessage("Arret du composant panneau solaire...") ;
		super.finalise();
	}
	
	@Override
	public void	shutdown() throws ComponentShutdownException
	{
		try {
			
			PortI[] p1 = this.findPortsFromInterface(IPanneau.class);
			PortI[] p2 = this.findPortsFromInterface(IAjoutUniteProduction.class);
			PortI[] p3 = this.findPortsFromInterface(IProduction.class);
			PortI[] port_assembleur = this.findPortsFromInterface(IComposantDynamique.class);
			
			p1[0].unpublishPort();
			p2[0].unpublishPort();
			p3[0].unpublishPort();
			port_assembleur[0].unpublishPort();
			
		} catch (Exception e) { throw new ComponentShutdownException(e); }
		super.shutdown();
	}

	@Override
	public void shutdownNow() throws ComponentShutdownException
	{
		try {
			
			PortI[] p1 = this.findPortsFromInterface(IPanneau.class);
			PortI[] p2 = this.findPortsFromInterface(IAjoutUniteProduction.class);
			PortI[] p3 = this.findPortsFromInterface(IProduction.class);
			PortI[] port_assembleur = this.findPortsFromInterface(IComposantDynamique.class);
			
			p1[0].unpublishPort();
			p2[0].unpublishPort();
			p3[0].unpublishPort();
			port_assembleur[0].unpublishPort();
			
		} catch (Exception e) { throw new ComponentShutdownException(e); }
		super.shutdownNow();
	}
	
	// ******************* Simulation *************************

	@Override
	protected Architecture createLocalArchitecture(String architectureURI) throws Exception {
		return PanneauSolaireCoupledModel.build();
	}

	@Override
	public Object getEmbeddingComponentStateValue(String name) throws Exception {
		if(name.equals(PanneauSolaireModel.URI + " : state")) {
			return etat;
		} else if(name.equals(PanneauSolaireModel.URI + " : energy")) {
			return production;
		}
		return null;
	}
	
	protected void initialise() throws Exception {
		Architecture localArchitecture = this.createLocalArchitecture(null) ;
		this.asp = new PanneauSolaireSimulatorPlugin();
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
