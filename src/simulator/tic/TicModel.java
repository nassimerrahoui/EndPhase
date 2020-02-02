package simulator.tic;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;

/**
 * @author Willy Nassim
 */

@ModelExternalEvents(exported = { TicEvent.class })
// -----------------------------------------------------------------------------
public class TicModel extends AtomicModel {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	/** name of the run parameter defining the delay between tic events. */
	public static final String DELAY_PARAMETER_NAME = "delay";
	/** the standard delay between tic events. */
	public static Duration STANDARD_DURATION = new Duration(60.0, TimeUnit.SECONDS);
	/** the URI to be used when creating the instance of the model. */
	public static final String URI = "TicModel";
	/**
	 * the value of the delay between tic events during the current simulation run.
	 */
	protected Duration delay;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a new model.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	simulatedTimeUnit != null
	 * pre	simulationEngine == null ||
	 * 		    	simulationEngine instanceof AtomicEngine
	 * post	this.getURI() != null
	 * post	uri != null implies this.getURI().equals(uri)
	 * post	this.getSimulatedTimeUnit().equals(simulatedTimeUnit)
	 * post	simulationEngine != null implies
	 * 			this.getSimulationEngine().equals(simulationEngine)
	 * </pre>
	 *
	 * @param uri               unique identifier of the model.
	 * @param simulatedTimeUnit time unit used for the simulation clock.
	 * @param simulationEngine  simulation engine enacting the model.
	 * @throws Exception <i>todo.</i>
	 */
	public TicModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		this.delay = TicModel.STANDARD_DURATION;
		this.setLogger(new StandardLogger());
		this.toggleDebugMode();
	}

	// -------------------------------------------------------------------------
	// Simulation protocol and related methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void setSimulationRunParameters(Map<String, Object> simParams) throws Exception {
		super.setSimulationRunParameters(simParams);

		String varName = this.getURI() + ":" + TicModel.DELAY_PARAMETER_NAME;
		if (simParams.containsKey(varName)) {
			this.delay = (Duration) simParams.get(varName);
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output()
	 */
	@Override
	public ArrayList<EventI> output() {
		ArrayList<EventI> ret = new ArrayList<EventI>();
		// compute the current simulation time because it has not been
		// updated yet.
		Time t = this.getCurrentStateTime().add(this.getNextTimeAdvance());
		TicEvent e = new TicEvent(t);
		this.logMessage("output " + e.eventAsString());
		// create the external event.
		ret.add(e);
		// return the new tic event.
		return ret;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void userDefinedInternalTransition(Duration elapsedTime) {
		this.logMessage("at internal transition " + this.getCurrentStateTime().getSimulatedTime() + " "
				+ elapsedTime.getSimulatedDuration());
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance()
	 */
	@Override
	public Duration timeAdvance() {
		return this.delay;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#getFinalReport()
	 */
	@Override
	public SimulationReportI getFinalReport() throws Exception {
		final String uri = this.getURI();
		return new SimulationReportI() {
			private static final long serialVersionUID = 1L;

			/**
			 * @see fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI#getModelURI()
			 */
			@Override
			public String getModelURI() {
				return uri;
			}

			/**
			 * @see java.lang.Object#toString()
			 */
			@Override
			public String toString() {
				return "TicModelReport()";
			}
		};
	}
}
// -----------------------------------------------------------------------------
