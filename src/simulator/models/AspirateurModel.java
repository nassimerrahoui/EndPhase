package simulator.models;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import app.util.ModeAspirateur;
import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentStateAccessI;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOAwithEquations;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.AbstractSimulationReport;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.utils.PlotterDescription;
import fr.sorbonne_u.utils.XYPlotter;
import simulator.events.AbstractAspirateurEvent;
import simulator.events.SetPerformanceMaximale;
import simulator.events.SetPerformanceReduite;
import simulator.events.SwitchOff;
import simulator.events.SwitchOn;

@ModelExternalEvents(imported = { 
		SwitchOn.class, 
		SwitchOff.class, 
		SetPerformanceReduite.class,
		SetPerformanceMaximale.class })

public class AspirateurModel extends AtomicHIOAwithEquations {

	public static class AspirateurReport extends AbstractSimulationReport {
		private static final long serialVersionUID = 1L;

		public AspirateurReport(String modelURI) {
			super(modelURI);
		}

		@Override
		public String toString() {
			return "AspirateurReport(" + this.getModelURI() + ")";
		}
	}

	private static final long serialVersionUID = 1L;
	public static final String URI = "AspirateurModel";

	private static final String SERIES = "intensity";

	protected static final double CONSOMMATION_PERFORMANCE_REDUITE = 800.0; // Watts
	protected static final double CONSOMMATION_PERFORMANCE_MAXIMALE = 1200.0; // Watts
	protected static final double TENSION = 220.0; // Volts

	/** current intensity in Amperes; intensity is power/tension. */
	@ExportedVariable(type = Double.class)
	protected final Value<Double> currentIntensity = new Value<Double>(this, 0.0, 0);
	protected ModeAspirateur currentState;

	protected XYPlotter intensityPlotter;

	protected EmbeddingComponentStateAccessI componentRef;

	public AspirateurModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		PlotterDescription pd = new PlotterDescription("Intensite Aspirateur", "Temps (sec)", "Intensite (Amp)", 100, 0,
				600, 400);
		this.intensityPlotter = new XYPlotter(pd);
		this.intensityPlotter.createSeries(SERIES);

		this.setLogger(new StandardLogger());
	}

	@Override
	public void setSimulationRunParameters(Map<String, Object> simParams) throws Exception {
		this.componentRef = (EmbeddingComponentStateAccessI) simParams.get("componentRef");
	}

	@Override
	public void initialiseState(Time initialTime) {
		this.currentState = ModeAspirateur.OFF;
		this.intensityPlotter.initialise();
		this.intensityPlotter.showPlotter();

		try {
			this.setDebugLevel(1);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		super.initialiseState(initialTime);
	}

	@Override
	protected void initialiseVariables(Time startTime) {
		this.currentIntensity.v = 0.0;
		this.intensityPlotter.addData(SERIES, this.getCurrentStateTime().getSimulatedTime(), this.getIntensity());

		super.initialiseVariables(startTime);
	}

	@Override
	public Vector<EventI> output() {
		// the model does not export any event.
		return null;
	}

	@Override
	public Duration timeAdvance() {
		if (this.componentRef == null) {
			return Duration.INFINITY;
		} else {
			return new Duration(10.0, TimeUnit.SECONDS);
		}
	}

	@Override
	public void userDefinedInternalTransition(Duration elapsedTime) {
		if (this.componentRef != null) {
			try {
				this.logMessage("component state = " + componentRef.getEmbeddingComponentStateValue("state"));
				this.logMessage("component intensity = " + componentRef.getEmbeddingComponentStateValue("intensity"));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void userDefinedExternalTransition(Duration elapsedTime) {
		if (this.hasDebugLevel(2)) {
			this.logMessage("AspirateurModel::userDefinedExternalTransition 1");
		}

		Vector<EventI> currentEvents = this.getStoredEventAndReset();
		assert currentEvents != null && currentEvents.size() == 1;

		Event ce = (Event) currentEvents.get(0);
		assert ce instanceof AbstractAspirateurEvent;
		if (this.hasDebugLevel(2)) {
			this.logMessage("AspirateurModel::userDefinedExternalTransition 2 " + ce.getClass().getCanonicalName());
		}

		this.intensityPlotter.addData(SERIES, this.getCurrentStateTime().getSimulatedTime(), this.getIntensity());

		if (this.hasDebugLevel(2)) {
			this.logMessage("AspirateurModel::userDefinedExternalTransition 3 " + this.getState());
		}

		ce.executeOn(this);

		if (this.hasDebugLevel(1)) {
			this.logMessage("AspirateurModel::userDefinedExternalTransition 4 " + this.getState());
		}

		this.intensityPlotter.addData(SERIES, this.getCurrentStateTime().getSimulatedTime(), this.getIntensity());

		super.userDefinedExternalTransition(elapsedTime);
		if (this.hasDebugLevel(2)) {
			this.logMessage("AspirateurModel::userDefinedExternalTransition 5");
		}
	}

	@Override
	public void endSimulation(Time endTime) throws Exception {
		this.intensityPlotter.addData(SERIES, endTime.getSimulatedTime(), this.getIntensity());
		Thread.sleep(10000L);
		this.intensityPlotter.dispose();

		super.endSimulation(endTime);
	}

	@Override
	public SimulationReportI getFinalReport() throws Exception {
		return new AspirateurReport(this.getURI());
	}

	public void setState(ModeAspirateur s) {
		this.currentState = s;
		switch (s) {
		case OFF:
			this.currentIntensity.v = 0.0;
			break;
		case PERFORMANCE_REDUITE:
			this.currentIntensity.v = CONSOMMATION_PERFORMANCE_REDUITE / TENSION;
			break;
		case PERFORMANCE_MAXIMALE:
			this.currentIntensity.v = CONSOMMATION_PERFORMANCE_MAXIMALE / TENSION;
		default:
			// cannot happening
			break;
		}
	}

	public ModeAspirateur getState() {
		return this.currentState;
	}

	public double getIntensity() {
		return this.currentIntensity.v;
	}
}