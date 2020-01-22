package simulator.models.aspirateur;

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
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.AbstractSimulationReport;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.utils.PlotterDescription;
import fr.sorbonne_u.utils.XYPlotter;
import simulator.events.aspirateur.AbstractAspirateurEvent;
import simulator.events.aspirateur.SendAspirateurConsommation;
import simulator.events.aspirateur.SetPerformanceMaximale;
import simulator.events.aspirateur.SetPerformanceReduite;
import simulator.events.aspirateur.SwitchAspirateurOff;
import simulator.events.aspirateur.SwitchAspirateurOn;
import simulator.tic.TicEvent;

@ModelExternalEvents(
		imported = { 
			SwitchAspirateurOn.class, 
			SwitchAspirateurOff.class, 
			SetPerformanceReduite.class,
			SetPerformanceMaximale.class
		},
		exported = {
			SendAspirateurConsommation.class
		})

public class AspirateurModel extends AtomicHIOAwithEquations {
	
	public static class AspirateurReport extends AbstractSimulationReport {
		private static final long serialVersionUID = 1L;
		protected final Vector<SendAspirateurConsommation> readings;
		
		public AspirateurReport(String modelURI, Vector<SendAspirateurConsommation> readings) {
			super(modelURI);
			this.readings = readings;
		}

		@Override
		public String toString() {
			return "AspirateurReport(" + this.getModelURI() + ")";
		}
	}

	private static final long serialVersionUID = 1L;
	public static final String URI = "AspirateurModel";
	public static final String COMPONENT_REF = "aspirateur-component-ref";
	public static final String POWER_PLOTTING_PARAM_NAME = "consommation";
	
	private static final String SERIES_POWER = "power";
	protected static final double CONSOMMATION_PERFORMANCE_REDUITE = 800.0/3.6; // Watts
	protected static final double CONSOMMATION_PERFORMANCE_MAXIMALE = 1200.0/3.6; // Watts
	protected static final double TENSION = 220.0; // Volts
	
	@ExportedVariable(type = Double.class)
	protected Value<Double> currentConsommation = new Value<Double>(this, 0.0, 0); // Watts
	protected ModeAspirateur currentState;
	protected XYPlotter powerPlotter;
	protected EmbeddingComponentStateAccessI componentRef;
	
	/** true when a external event triggered a reading. */
	protected boolean triggerReading;
	/** the last value emitted as a reading of the solar solarIntensity. */
	protected double lastReading;
	/** the simulation time at the last reading. */
	protected double lastReadingTime;
	/** history of readings, for the simulation report. */
	protected final Vector<SendAspirateurConsommation> readings;
	
	

	public AspirateurModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		this.setLogger(new StandardLogger());
		readings = new Vector<SendAspirateurConsommation>();
		lastReading = -1.0;
	}

	@Override
	public void setSimulationRunParameters(Map<String, Object> simParams) throws Exception {
		this.componentRef = (EmbeddingComponentStateAccessI) simParams.get(URI + " : " + COMPONENT_REF);
	
		PlotterDescription pd = (PlotterDescription) simParams.get(URI + " : " + POWER_PLOTTING_PARAM_NAME);
		this.powerPlotter = new XYPlotter(pd);
		this.powerPlotter.createSeries(SERIES_POWER);
	}

	@Override
	public void initialiseState(Time initialTime) {
		this.currentState = ModeAspirateur.OFF;	
		this.powerPlotter.initialise();
		this.powerPlotter.showPlotter();
		
		this.triggerReading = false;
		this.lastReadingTime = initialTime.getSimulatedTime();
		this.readings.clear();

		try {
			//this.setDebugLevel(1);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		super.initialiseState(initialTime);
	}

	@Override
	protected void initialiseVariables(Time startTime) {
		this.powerPlotter.addData(SERIES_POWER, this.getCurrentStateTime().getSimulatedTime(), this.getConsommation());
		super.initialiseVariables(startTime);
	}

	@Override
	public Vector<EventI> output() {
		if (this.triggerReading) {

			this.lastReading = this.currentConsommation.v;
			this.lastReadingTime = this.getCurrentStateTime().getSimulatedTime();

			Vector<EventI> ret = new Vector<EventI>(1);
			Time t = this.getCurrentStateTime().add(this.getNextTimeAdvance());
			SendAspirateurConsommation bl = new SendAspirateurConsommation(t, currentConsommation.v);
			ret.add(bl);

			this.readings.add(bl);
			// Trace the execution
			this.logMessage(this.getCurrentStateTime() + "|output|Aspirateur Consommation reading " + this.readings.size()
					+ " with value = " + this.currentConsommation.v);

			this.triggerReading = false;
			return ret;
		} else {
			return null;
		}
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
		this.powerPlotter.addData(SERIES_POWER, this.getCurrentStateTime().getSimulatedTime(), this.getConsommation());
		super.userDefinedInternalTransition(elapsedTime) ;
	}

	@Override
	public void userDefinedExternalTransition(Duration elapsedTime) {
		
		Vector<EventI> currentEvents = this.getStoredEventAndReset();
		assert currentEvents != null;

		for (EventI ce : currentEvents) {
			if(ce instanceof AbstractAspirateurEvent) {
				this.powerPlotter.addData(SERIES_POWER, this.getCurrentStateTime().getSimulatedTime(), this.getConsommation());
				ce.executeOn(this);
				this.powerPlotter.addData(SERIES_POWER, this.getCurrentStateTime().getSimulatedTime(), this.getConsommation());
		
			} else if(ce instanceof TicEvent) {

				this.triggerReading = true;
			}
		}
		super.userDefinedExternalTransition(elapsedTime);
	}

	@Override
	public void endSimulation(Time endTime) throws Exception {
		this.powerPlotter.addData(SERIES_POWER, endTime.getSimulatedTime(), this.getConsommation());
		Thread.sleep(10000L);
		this.powerPlotter.dispose();

		super.endSimulation(endTime);
	}

	@Override
	public SimulationReportI getFinalReport() throws Exception {
		return new AspirateurReport(this.getURI(), this.readings);
	}

	public void setState(ModeAspirateur s) {
		this.currentState = s;
		switch (s) {
		case OFF:
			this.currentConsommation.v = 0.0;
			break;
		case PERFORMANCE_REDUITE:
			this.currentConsommation.v = CONSOMMATION_PERFORMANCE_REDUITE;
			break;
		case PERFORMANCE_MAXIMALE:
			this.currentConsommation.v = CONSOMMATION_PERFORMANCE_MAXIMALE;
			break;
		default:
			// cannot happen
			break;
		}
	}

	public ModeAspirateur getState() {
		return this.currentState;
	}

	public double getConsommation() {
		return this.currentConsommation.v;
	}
}