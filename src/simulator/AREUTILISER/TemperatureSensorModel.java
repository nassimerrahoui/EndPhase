//package simulator.AREUTILISER;
//
//import java.util.Map;
//import java.util.Vector;
//import java.util.concurrent.TimeUnit;
//
//import fr.sorbonne_u.devs_simulation.hioa.annotations.ImportedVariable;
//import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOAwithEquations;
//import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
//import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
//import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
//import fr.sorbonne_u.devs_simulation.models.events.EventI;
//import fr.sorbonne_u.devs_simulation.models.time.Duration;
//import fr.sorbonne_u.devs_simulation.models.time.Time;
//import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
//import fr.sorbonne_u.devs_simulation.utils.AbstractSimulationReport;
//import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
//import fr.sorbonne_u.utils.PlotterDescription;
//import fr.sorbonne_u.utils.XYPlotter;
//import simulator.models.frigo.FrigoModel;
//import simulator.tic.TicEvent;
//
//
//@ModelExternalEvents(imported = {TicEvent.class},
//					exported = {Temperature.class})
//
//public class TemperatureSensorModel extends AtomicHIOAwithEquations {
//
//	public static class TemperatureSensorReport extends AbstractSimulationReport {
//		private static final long serialVersionUID = 1L;
//		protected final Vector<Temperature> readings;
//
//		public TemperatureSensorReport(String modelURI, Vector<Temperature> readings) {
//			super(modelURI);
//
//			this.readings = readings;
//		}
//
//		@Override
//		public String toString() {
//			String ret = "\n-----------------------------------------\n";
//			ret += "Temperature Level Sensor Report\n";
//			ret += "-----------------------------------------\n";
//			ret += "number of readings = " + this.readings.size() + "\n";
//			ret += "Readings:\n";
//			for (int i = 0; i < this.readings.size(); i++) {
//				ret += "    " + this.readings.get(i).eventAsString() + "\n";
//			}
//			ret += "-----------------------------------------\n";
//			return ret;
//		}
//	}
//	
//	private static final long				serialVersionUID = 1L ;
//	/** an URI to be used when create an instance of the model.				*/
//	public static final String				URI = "TemperatureSensorModel" ;
//
//	/** true when a external event triggered a reading.						*/
//	protected boolean						triggerReading ;
//	/** the last value emitted as a reading of the temperature level.		 	*/
//	protected double						lastReading ;
//	/** the simulation time at the last reading.							*/
//	protected double						lastReadingTime ;
//	/** history of readings, for the simulation report.						*/
//	protected final Vector<Temperature>	readings ;
//
//	/** frame used to plot the temperature level readings during the
//	 *  simulation.															*/
//	protected XYPlotter						plotter ;
//	
//
//	@ImportedVariable(type = Double.class)
//	protected Value<Double>					remainingCapacity ;
//	
//	
//	
//	public TemperatureSensorModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine)
//			throws Exception {
//		super(uri, simulatedTimeUnit, simulationEngine);
//
//		this.setLogger(new StandardLogger());
//		this.readings = new Vector<Temperature>();
//		this.lastReading = -1.0;
//	}
//	
//	public void setSimulationRunParameters(Map<String, Object> simParams) throws Exception {
//		String vname = this.getURI() + ":" + PlotterDescription.PLOTTING_PARAM_NAME;
//		PlotterDescription pd = (PlotterDescription) simParams.get(vname);
//		this.plotter = new XYPlotter(pd);
//		this.plotter.createSeries("standard");
//	}
//	
//
//	@Override
//	public void initialiseState(Time initialTime) {
//		this.triggerReading = false;
//		this.lastReadingTime = initialTime.getSimulatedTime();
//		this.readings.clear();
//		if (this.plotter != null) {
//			this.plotter.initialise();
//			this.plotter.showPlotter();
//		}
//
//		super.initialiseState(initialTime);
//	}
//
//	@Override
//	protected void initialiseVariables(Time startTime) {
//		this.remainingCapacity.v = FrigoModel.AMBIENT_TEMPERATURE;
//
//		super.initialiseVariables(startTime);
//	}
//
//	@Override
//	public Duration timeAdvance() {
//		if (this.triggerReading) {
//			// immediate internal event when a reading is triggered.
//			return Duration.zero(this.getSimulatedTimeUnit());
//		} else {
//			return Duration.INFINITY;
//		}
//	}
//	
//	@Override
//	public Vector<EventI> output() {
//		if (this.triggerReading) {
//			// Plotting, plays no role in the simulation
//			if (this.plotter != null) {
//				this.plotter.addData("standard", this.lastReadingTime, this.remainingCapacity.v);
//				this.plotter.addData("standard", this.getCurrentStateTime().getSimulatedTime(),
//						this.remainingCapacity.v);
//			}
//			// Memorise a new last reading
//			this.lastReading = this.remainingCapacity.v;
//			this.lastReadingTime = this.getCurrentStateTime().getSimulatedTime();
//
//			// Create and emit the temperature level event.
//			Vector<EventI> ret = new Vector<EventI>(1);
//			Time t = this.getCurrentStateTime().add(this.getNextTimeAdvance());
//			Temperature bl = new Temperature(t, this.remainingCapacity.v);
//			ret.add(bl);
//
//			// Memorise the reading for the simulation report.
//			this.readings.add(bl);
//			// Trace the execution
//			this.logMessage(this.getCurrentStateTime() + "|output|temperature reading " + this.readings.size()
//					+ " with value = " + this.remainingCapacity.v);
//
//			// The reading that was triggered has now been processed.
//			this.triggerReading = false;
//			return ret;
//		} else {
//			return null;
//		}
//	}
//
//	@Override
//	public void userDefinedInternalTransition(Duration elapsedTime) {
//		super.userDefinedInternalTransition(elapsedTime);
//		if (this.hasDebugLevel(1)) {
//			this.logMessage(this.getCurrentStateTime() + "|internal|temperature = " + this.remainingCapacity.v + " mAh.");
//		}
//	}
//	
//	
//	@Override
//	public void userDefinedExternalTransition(Duration elapsedTime) {
//		super.userDefinedExternalTransition(elapsedTime);
//
//		Vector<EventI> current = this.getStoredEventAndReset();
//		boolean ticReceived = false;
//		for (int i = 0; !ticReceived && i < current.size(); i++) {
//			if (current.get(i) instanceof TicEvent) {
//				ticReceived = true;
//			}
//		}
//		if (ticReceived) {
//			this.triggerReading = true;
//		}
//	}
//
//	@Override
//	public SimulationReportI getFinalReport() throws Exception {
//		return new TemperatureSensorReport(this.getURI(), this.readings);
//	}
//		
//}
