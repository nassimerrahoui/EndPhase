package simulator.models.compteur;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.utils.PlotterDescription;
import fr.sorbonne_u.utils.XYPlotter;
import simulator.events.aspirateur.SendAspirateurConsommation;
import simulator.events.frigo.SendFrigoConsommation;

@ModelExternalEvents(
		imported = {
				SendAspirateurConsommation.class,
				SendFrigoConsommation.class })
public class CompteurModel extends AtomicModel {

	private static final long serialVersionUID = 1L;

	public static final String URI = "CompteurModel";
	public static final String COMPONENT_REF = "compteur-component-ref";
	public static final String CONSOMMATION_PLOTTING_PARAM_NAME = "consommation";
	public static final String PRODUCTION_PLOTTING_PARAM_NAME = "production";
	
	private static final String SERIES_CONSOMMATION = "compteur_consommation";
	private static final String SERIES_PRODUCTION = "compteur_production";
	
	protected XYPlotter consommationPlotter;
	protected XYPlotter productionPlotter;
	
	protected double consommation_globale;
	protected double production_globale;
	
	/** Reference du composant associe au modele */
	protected EmbeddingComponentAccessI componentRef;
	
	protected ConcurrentHashMap<String, Double> appareil_consommation;
	protected ConcurrentHashMap<String, Double> unite_production;
	
	
	public CompteurModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		this.setLogger(new StandardLogger());
	}
	
	@Override
	public void setSimulationRunParameters(Map<String, Object> simParams) throws Exception {
		this.componentRef = (EmbeddingComponentAccessI) simParams.get(URI + " : " + COMPONENT_REF);
		
		PlotterDescription pd = (PlotterDescription) simParams.get(CompteurModel.URI + " : " + CompteurModel.CONSOMMATION_PLOTTING_PARAM_NAME) ;
		this.consommationPlotter = new XYPlotter(pd);
		this.consommationPlotter.createSeries(SERIES_CONSOMMATION);
		
		pd = (PlotterDescription) simParams.get(URI + " : " + PRODUCTION_PLOTTING_PARAM_NAME) ;
		this.productionPlotter = new XYPlotter(pd);
		this.productionPlotter.createSeries(SERIES_PRODUCTION);
	}

	@Override
	public void initialiseState(Time initialTime) {
		this.consommation_globale = 0.0;
		this.production_globale = 0.0;
		this.appareil_consommation = new ConcurrentHashMap<>();
		this.unite_production = new ConcurrentHashMap<>();
		
		if(this.consommationPlotter != null) {
			this.consommationPlotter.initialise();
			this.consommationPlotter.showPlotter();
		}
		
		if(this.productionPlotter != null) {
			this.productionPlotter.initialise();
			this.productionPlotter.showPlotter();
		}
		
		
		super.initialiseState(initialTime);
		
		this.consommationPlotter.addData(SERIES_CONSOMMATION, this.getCurrentStateTime().getSimulatedTime(), this.consommation_globale);
		this.productionPlotter.addData(SERIES_PRODUCTION, initialTime.getSimulatedTime(), this.production_globale);
	}

	@Override
	public ArrayList<EventI> output() {
		// No exported event
		return null;
	}

	@Override
	public Duration timeAdvance() {
		if (this.componentRef == null) {
			return Duration.INFINITY;
		} else {
			return new Duration(1.0, TimeUnit.SECONDS);
		}
	}
	
	@Override
	public void userDefinedInternalTransition(Duration elapsedTime) {
		this.consommationPlotter.addData(SERIES_CONSOMMATION, this.getCurrentStateTime().getSimulatedTime(), this.consommation_globale);
		this.productionPlotter.addData(SERIES_PRODUCTION, this.getCurrentStateTime().getSimulatedTime(), this.production_globale);
		
		super.userDefinedInternalTransition(elapsedTime) ;
	}
	
	@Override
	public void userDefinedExternalTransition(Duration elapsedTime) {
		super.userDefinedExternalTransition(elapsedTime);
		ArrayList<EventI> current = this.getStoredEventAndReset();
		
		// ici, la consommation/production est stocke par type d'appareil (resp. type d'unite de production)
		// il faudrait utiliser l'uri comme cle pour avoir plusieurs appareils/unite de production du meme type
		// nous avons simplifie volontairement les maps de stockage de consommation/production pour l'exemple
		for (int i = 0 ; i < current.size() ; i++) {
			if(current.get(i) instanceof SendAspirateurConsommation) {
				double conso = ((SendAspirateurConsommation.Reading)
						((SendAspirateurConsommation) current.get(i)).
						getEventInformation()).value;
				appareil_consommation.put(SendAspirateurConsommation.class.getName(), conso);
			} else if(current.get(i) instanceof SendFrigoConsommation) {
				double conso = ((SendFrigoConsommation.Reading)
						((SendFrigoConsommation) current.get(i)).
						getEventInformation()).value;
				appareil_consommation.put(SendFrigoConsommation.class.getName(), conso);
			}
		}
		
		try {
			this.consommation_globale = getConsommationGlobale();
			this.production_globale = getProductionGlobale();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.consommationPlotter.addData(SERIES_CONSOMMATION, this.getCurrentStateTime().getSimulatedTime(), this.consommation_globale);
		this.productionPlotter.addData(SERIES_PRODUCTION, this.getCurrentStateTime().getSimulatedTime(), this.production_globale);
	}
	
	@Override
	public void endSimulation(Time endTime) throws Exception {
		this.consommationPlotter.addData(SERIES_CONSOMMATION, endTime.getSimulatedTime(), this.consommation_globale);
		this.productionPlotter.addData(SERIES_PRODUCTION, endTime.getSimulatedTime(), this.production_globale);
		
		Thread.sleep(10000L);
		
		this.consommationPlotter.dispose();
		this.productionPlotter.dispose();

		super.endSimulation(endTime);
	}
	
	
	/**
	 * Retourne la consommation globale des appareils
	 * @return
	 * @throws Exception
	 */
	public double getConsommationGlobale() throws Exception {
		double res = appareil_consommation.values().stream().mapToDouble(i -> i).sum();
		if(res > 0.0) return res;
		else return 0.0;
	}

	/**
	 * Retourne la production totale des unites de productions
	 * @return
	 * @throws Exception
	 */
	public double getProductionGlobale() throws Exception {
		double res = unite_production.values().stream().mapToDouble(i -> i).sum();
		if(res > 0.0) return res;
		else return 0.0;
	}
}
