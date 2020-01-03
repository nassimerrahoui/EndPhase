package simulator.models.panneausolaire;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
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
import simulator.events.panneausolaire.Energie;
import simulator.tic.TicEvent;

@ModelExternalEvents(imported = { TicEvent.class }, exported = { Energie.class })

public class PanneauSolaireSensorModel extends AtomicHIOAwithEquations {

	public static class PanneauSolaireSensorReport extends AbstractSimulationReport {
		private static final long serialVersionUID = 1L;
		protected final Vector<Energie> readings;

		public PanneauSolaireSensorReport(String modelURI, Vector<Energie> readings) {
			super(modelURI);

			this.readings = readings;
		}

		@Override
		public String toString() {
			String ret = "\n-----------------------------------------\n";
			ret += "Rapport du niveau d'energie \n";
			ret += "-----------------------------------------\n";
			ret += "Nombre de lectures = " + this.readings.size() + "\n";
			ret += "Lectures :\n";
			for (int i = 0; i < this.readings.size(); i++) {
				ret += "    " + this.readings.get(i).eventAsString() + "\n";
			}
			ret += "-----------------------------------------\n";
			return ret;
		}
	}

	private static final long serialVersionUID = 1L;
	public static final String URI = "PanneauSolaireSensorModel";
	
	/** true when a external event triggered a reading. */
	protected boolean triggerReading;
	/** the last value emitted as a reading of the temperature level. */
	protected double lastReading;
	/** the simulation time at the last reading. */
	protected double lastReadingTime;
	/** history of readings, for the simulation report. */
	protected final Vector<Energie> readings;

	/**
	 * frame used to plot the temperature level readings during the simulation.
	 */
	protected XYPlotter plotter;

	@ExportedVariable(type = Double.class)
	protected Value<Double> energy;

	public PanneauSolaireSensorModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine)
			throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);

		this.setLogger(new StandardLogger());
		this.readings = new Vector<Energie>();
		this.lastReading = -1.0;
	}

	public void setSimulationRunParameters(Map<String, Object> simParams) throws Exception {
		String vname = this.getURI() + ":" + PlotterDescription.PLOTTING_PARAM_NAME;
		PlotterDescription pd = (PlotterDescription) simParams.get(vname);
		this.plotter = new XYPlotter(pd);
		this.plotter.createSeries("standard");
	}

	@Override
	public void initialiseState(Time initialTime) {
		this.triggerReading = false;
		this.lastReadingTime = initialTime.getSimulatedTime();
		this.readings.clear();
		if (this.plotter != null) {
			this.plotter.initialise();
			this.plotter.showPlotter();
		}

		super.initialiseState(initialTime);
	}

	@Override
	protected void initialiseVariables(Time startTime) {
		this.energy.v = 0.0;

		super.initialiseVariables(startTime);
	}

	@Override
	public Duration timeAdvance() {
		if (this.triggerReading) {
			// immediate internal event when a reading is triggered.
			return Duration.zero(this.getSimulatedTimeUnit());
		} else {
			return Duration.INFINITY;
		}
	}

	@Override
	public Vector<EventI> output() {
		if (this.triggerReading) {
			// Plotting, plays no role in the simulation
			if (this.plotter != null) {
				this.plotter.addData("standard", this.lastReadingTime, this.energy.v);
				this.plotter.addData("standard", this.getCurrentStateTime().getSimulatedTime(),
						this.energy.v);
			}
			// Memorise a new last reading
			this.lastReading = this.energy.v;
			this.lastReadingTime = this.getCurrentStateTime().getSimulatedTime();

			// Create and emit the temperature level event.
			Vector<EventI> ret = new Vector<EventI>(1);
			Time t = this.getCurrentStateTime().add(this.getNextTimeAdvance());
			Energie bl = new Energie(t, this.energy.v);
			ret.add(bl);

			// Memorise the reading for the simulation report.
			this.readings.add(bl);
			// Trace the execution
			this.logMessage(this.getCurrentStateTime() + "|output|energy reading " + this.readings.size()
					+ " with value = " + this.energy.v);

			// The reading that was triggered has now been processed.
			this.triggerReading = false;
			return ret;
		} else {
			return null;
		}
	}

	@Override
	public void userDefinedInternalTransition(Duration elapsedTime) {
		super.userDefinedInternalTransition(elapsedTime);
		if (this.hasDebugLevel(1)) {
			this.logMessage(
					this.getCurrentStateTime() + "|internal|energy = " + this.energy.v + " Watt.");
		}
	}

	@Override
	public void userDefinedExternalTransition(Duration elapsedTime) {
		super.userDefinedExternalTransition(elapsedTime);

		Vector<EventI> current = this.getStoredEventAndReset();
		boolean ticReceived = false;
		for (int i = 0; !ticReceived && i < current.size(); i++) {
			if (current.get(i) instanceof TicEvent) {
				ticReceived = true;
			}
		}
		if (ticReceived) {
			this.triggerReading = true;
		}
	}

	@Override
	public SimulationReportI getFinalReport() throws Exception {
		return new PanneauSolaireSensorReport(this.getURI(), this.readings);
	}

}
