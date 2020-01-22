package simulator.models.compteur;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentStateAccessI;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.utils.PlotterDescription;
import fr.sorbonne_u.utils.XYPlotter;
import simulator.events.aspirateur.SendAspirateurConsommation;

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
	protected EmbeddingComponentStateAccessI componentRef;
	
	
	public CompteurModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		this.setLogger(new StandardLogger());
	}
	
	@Override
	public void setSimulationRunParameters(Map<String, Object> simParams) throws Exception {
		
		this.componentRef = (EmbeddingComponentStateAccessI) simParams.get(URI + " : " + COMPONENT_REF);

		PlotterDescription pd = (PlotterDescription) simParams.get(URI + " : " + CONSOMMATION_PLOTTING_PARAM_NAME) ;
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
	public Vector<EventI> output() {
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
		super.userDefinedInternalTransition(elapsedTime) ;

		this.consommationPlotter.addData(SERIES_CONSOMMATION, this.getCurrentStateTime().getSimulatedTime(), this.consommation_globale);
		this.productionPlotter.addData(SERIES_PRODUCTION, this.getCurrentStateTime().getSimulatedTime(), this.production_globale);

	}
	
	@Override
	public void userDefinedExternalTransition(Duration elapsedTime) {
		Vector<EventI> current = this.getStoredEventAndReset();
		assert current != null;
		
		for (int i = 0 ; i < current.size() ; i++) {
			if(current.get(i) instanceof SendAspirateurConsommation)
				this.consommation_globale += ((SendAspirateurConsommation.Reading)
						((SendAspirateurConsommation) current.get(i)).
						getEventInformation()).value;
		}
		
		this.consommationPlotter.addData(SERIES_CONSOMMATION, this.getCurrentStateTime().getSimulatedTime(), this.consommation_globale);
		this.productionPlotter.addData(SERIES_PRODUCTION, this.getCurrentStateTime().getSimulatedTime(), this.production_globale);
		
		
		super.userDefinedExternalTransition(elapsedTime);

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
	
	
	public double getConsommationGlobale() {
		return this.consommation_globale;
	}
	
	public double getProductionGlobale() {
		return this.production_globale;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
