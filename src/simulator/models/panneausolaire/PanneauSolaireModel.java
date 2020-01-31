package simulator.models.panneausolaire;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import app.util.EtatUniteProduction;
import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ImportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOAwithEquations;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.AbstractSimulationReport;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.utils.PlotterDescription;
import fr.sorbonne_u.utils.XYPlotter;
import simulator.events.panneausolaire.SendPanneauSolaireProduction;
import simulator.events.panneausolaire.SolarIntensity;

@ModelExternalEvents(imported = { SolarIntensity.class }, 
					exported = {SendPanneauSolaireProduction.class})

public class PanneauSolaireModel extends AtomicHIOAwithEquations {

	private static final long serialVersionUID = 1L;
	public static final String URI = "PanneauSolaireModel";
	public static final String COMPONENT_REF = "panneausolaire-component-ref";
	public static final String INTENSITY_PLOTTING_PARAM_NAME = "solarIntensity";
	
	private static final String SERIES_INTENSITY = "solarIntensity";
	protected Value<Double> currentEnergy = new Value<Double>(this, 0.0, 0); // Watts
	protected EtatUniteProduction currentState;
	
	@ImportedVariable(type = Double.class)
	protected Value<Double> currentSolarIntensity = new Value<Double>(this, 0.0, 0);
	
	protected XYPlotter intensityPlotter;
	protected EmbeddingComponentAccessI componentRef;
	
	public static class PanneauSolaireStateReport extends AbstractSimulationReport {
		private static final long serialVersionUID = 1L;

		public PanneauSolaireStateReport(String modelURI) {
			super(modelURI);
		}

		@Override
		public String toString() {
			return "PanneauSolaireStateReport(" + this.getModelURI() + ")";
		}
	}

	public PanneauSolaireModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		
		super(uri, simulatedTimeUnit, simulationEngine);
		this.setLogger(new StandardLogger());
	}

	@Override
	public void setSimulationRunParameters(Map<String, Object> simParams) throws Exception {
		this.componentRef = (EmbeddingComponentAccessI) simParams.get(URI + " : " + COMPONENT_REF);
	
		PlotterDescription pd = (PlotterDescription) simParams.get(URI + " : " + INTENSITY_PLOTTING_PARAM_NAME);
		this.intensityPlotter = new XYPlotter(pd);
		this.intensityPlotter.createSeries(SERIES_INTENSITY);
	}

	@Override
	public void initialiseState(Time initialTime) {
		this.currentState = EtatUniteProduction.ON;	
		this.intensityPlotter.initialise();
		this.intensityPlotter.showPlotter();
		
		super.initialiseState(initialTime);
	}

	@Override
	protected void initialiseVariables(Time startTime) {
		this.currentEnergy.v = 0.0;
		this.intensityPlotter.addData(SERIES_INTENSITY, this.getCurrentStateTime().getSimulatedTime(), this.getEnergy());
		super.initialiseVariables(startTime);
	}

	@Override
	public ArrayList<EventI> output() {
		ArrayList<EventI> ret = new ArrayList<EventI>() ;
		Time t = this.getCurrentStateTime().add(getNextTimeAdvance()) ;
		try {
			ret.add(new SendPanneauSolaireProduction(t,
					currentEnergy.v)) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
		
		return ret ;
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
		
		super.userDefinedInternalTransition(elapsedTime);
		this.intensityPlotter.addData(SERIES_INTENSITY, this.getCurrentStateTime().getSimulatedTime(), this.getEnergy());
	}

	@Override
	public void userDefinedExternalTransition(Duration elapsedTime) {
		
		ArrayList<EventI> current = this.getStoredEventAndReset();
		assert current != null;
		assert componentRef != null;

		for (int i = 0 ; i < current.size() ; i++) {
			if (current.get(i) instanceof SolarIntensity) {
				this.currentSolarIntensity.v = ((SolarIntensity.Reading) 
											((SolarIntensity) current.get(i)).
											getEventInformation()).value;
			}
		}
		
		try {
			this.setState((EtatUniteProduction) componentRef.getEmbeddingComponentStateValue(URI + " : state"));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		this.intensityPlotter.addData(SERIES_INTENSITY, this.getCurrentStateTime().getSimulatedTime(), this.getEnergy());

		super.userDefinedExternalTransition(elapsedTime);
	}

	@Override
	public void endSimulation(Time endTime) throws Exception {
		this.intensityPlotter.addData(SERIES_INTENSITY, endTime.getSimulatedTime(), this.getEnergy());
		Thread.sleep(10000L);
		this.intensityPlotter.dispose();

		super.endSimulation(endTime);
	}

	@Override
	public SimulationReportI getFinalReport() throws Exception {
		return new PanneauSolaireStateReport(this.getURI());
	}

	public void setState(EtatUniteProduction s) {
		this.currentState = s;
		switch (s) {
		case OFF:
			this.currentEnergy.v = 0.0;
			break;
		case ON:
			this.currentEnergy.v = currentSolarIntensity.v * 20;
			break;
		default:
			// cannot happen
			break;
		}
	}

	public EtatUniteProduction getState() {
		return this.currentState;
	}

	public double getEnergy() {
		return this.currentEnergy.v;
	}
}