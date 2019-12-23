package simulator.models;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import app.util.ModeOrdinateur;
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
import simulator.events.AbstractOrdinateurEvent;
import simulator.events.SetPerformanceMaximale;
import simulator.events.SetPerformanceReduite;
import simulator.events.SwitchOff;
import simulator.events.SwitchOn;

@ModelExternalEvents(imported = { SwitchOn.class, SwitchOff.class, SetPerformanceReduite.class,
		SetPerformanceMaximale.class })
public class OrdinateurModel extends AtomicHIOAwithEquations {

	public static class OrdinateurReport extends AbstractSimulationReport {
		private static final long serialVersionUID = 1L;

		public OrdinateurReport(String modelURI) {
			super(modelURI);
		}

		@Override
		public String toString() {
			return "OrdinateurReport(" + this.getModelURI() + ")";
		}
	}

	private static final long serialVersionUID = 1L;
	public static final String URI = "OrdinateurModel";

	private static final String SERIES = "intensity";

	protected static final double CONSOMMATION_PERFORMANCE_REDUITE = 800.0; // Watts
	protected static final double CONSOMMATION_PERFORMANCE_MAXIMALE = 1200.0; // Watts
	protected static final double TENSION = 220.0; // Volts

	/** current intensity in Amperes; intensity is power/tension. */
	@ExportedVariable(type = Double.class)
	protected final Value<Double> currentIntensity = new Value<Double>(this, 0.0, 0);
	protected ModeOrdinateur currentState;

	protected XYPlotter intensityPlotter;

	protected EmbeddingComponentStateAccessI componentRef;

	public OrdinateurModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		PlotterDescription pd = new PlotterDescription("Intensite Ordinateur", "Temps (sec)", "Intensite (Amp)", 100, 0,
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
		this.currentState = ModeOrdinateur.OFF;
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
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void userDefinedExternalTransition(Duration elapsedTime) {
		if (this.hasDebugLevel(2)) {
			this.logMessage("OrdinateurModel::userDefinedExternalTransition 1");
		}

		Vector<EventI> currentEvents = this.getStoredEventAndReset();
		assert currentEvents != null && currentEvents.size() == 1;

		Event ce = (Event) currentEvents.get(0);
		assert ce instanceof AbstractOrdinateurEvent;
		if (this.hasDebugLevel(2)) {
			this.logMessage("OrdinateurModel::userDefinedExternalTransition 2 " + ce.getClass().getCanonicalName());
		}

		this.intensityPlotter.addData(SERIES, this.getCurrentStateTime().getSimulatedTime(), this.getIntensity());

		if (this.hasDebugLevel(2)) {
			this.logMessage("OrdinateurModel::userDefinedExternalTransition 3 " + this.getState());
		}

		ce.executeOn(this);

		if (this.hasDebugLevel(1)) {
			this.logMessage("OrdinateurModel::userDefinedExternalTransition 4 " + this.getState());
		}

		this.intensityPlotter.addData(SERIES, this.getCurrentStateTime().getSimulatedTime(), this.getIntensity());

		super.userDefinedExternalTransition(elapsedTime);
		if (this.hasDebugLevel(2)) {
			this.logMessage("OrdinateurModel::userDefinedExternalTransition 5");
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
		return new OrdinateurReport(this.getURI());
	}

	public void setState(ModeOrdinateur s) {
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

	public ModeOrdinateur getState() {
		return this.currentState;
	}

	public double getIntensity() {
		return this.currentIntensity.v;
	}
}