package simulator.models.panneausolaire;

import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.random.RandomDataGenerator;

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
import simulator.events.panneausolaire.SolarIntensity;
import simulator.tic.TicEvent;

@ModelExternalEvents(imported = { TicEvent.class }, exported = { SolarIntensity.class })
public class PanneauSolaireSensorModel extends AtomicHIOAwithEquations {

	public static class PanneauSolaireSensorReport extends AbstractSimulationReport {
		private static final long serialVersionUID = 1L;
		protected final Vector<SolarIntensity> readings;

		public PanneauSolaireSensorReport(String modelURI, Vector<SolarIntensity> readings) {
			super(modelURI);
			this.readings = readings;
		}

		@Override
		public String toString() {
			String ret = "\n-----------------------------------------\n";
			ret += "Rapport du niveau d'intensite du soleil \n";
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
	/** the last value emitted as a reading of the solar solarIntensity. */
	protected double lastReading;
	/** the simulation time at the last reading. */
	protected double lastReadingTime;
	/** history of readings, for the simulation report. */
	protected final Vector<SolarIntensity> readings;

	@ExportedVariable(type = Double.class)
	protected Value<Double> solarIntensity = new Value<Double>(this, 0.0, 0);
	protected static final double KILO_WATT_CRETE = 2.0;
	protected final RandomDataGenerator	rgNewSolarIntensity;
	protected int nb_tic;
	
	public PanneauSolaireSensorModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine)
			throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		
		this.setLogger(new StandardLogger());
		this.readings = new Vector<SolarIntensity>();
		this.lastReading = -1.0;
		this.rgNewSolarIntensity = new RandomDataGenerator();
		this.nb_tic = 8;
	}

	@Override
	public void initialiseState(Time initialTime) {
		this.triggerReading = false;
		this.lastReadingTime = initialTime.getSimulatedTime();
		this.readings.clear();

		super.initialiseState(initialTime);
	}

	@Override
	public Duration timeAdvance() {
		if (this.triggerReading) {
			// immediate internal event when a reading is triggered.
			return Duration.zero(this.getSimulatedTimeUnit());
		} else {
			return Duration.one(this.getSimulatedTimeUnit());
		}
	}

	@Override
	public Vector<EventI> output() {
		if (this.triggerReading) {

			this.lastReading = this.solarIntensity.v;
			this.lastReadingTime = this.getCurrentStateTime().getSimulatedTime();

			Vector<EventI> ret = new Vector<EventI>(1);
			Time t = this.getCurrentStateTime().add(this.getNextTimeAdvance());
			SolarIntensity bl = new SolarIntensity(t, this.solarIntensity.v);
			ret.add(bl);

			this.readings.add(bl);
			// Trace the execution
			this.logMessage(this.getCurrentStateTime() + "|output|solarIntensity reading " + this.readings.size()
					+ " with value = " + this.solarIntensity.v);

			this.triggerReading = false;
			return ret;
		} else {
			return null;
		}
	}
	
	protected double generateSolarIntensity() {
		// Generate a random solar intensity using the Beta distribution 
		double newSolarIntensity = solarIntensity.v;
		if(nb_tic < 8 || nb_tic >= 40) {
			newSolarIntensity = 0.0;
		} else if(nb_tic >= 8 && nb_tic < 24) {
			newSolarIntensity += this.rgNewSolarIntensity.nextBeta(1.75, 1.75);
		} else {
			newSolarIntensity -= this.rgNewSolarIntensity.nextBeta(1.75, 1.75);
			if(newSolarIntensity < 0.0) {
				newSolarIntensity = 0.0;
			}
		}
		return newSolarIntensity;
	}

	@Override
	public void userDefinedInternalTransition(Duration elapsedTime) {
		super.userDefinedInternalTransition(elapsedTime);
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
			nb_tic = (nb_tic + 1) % 48;
			this.solarIntensity.v = generateSolarIntensity();
			this.triggerReading = true;
		}
	}

	@Override
	public SimulationReportI getFinalReport() throws Exception {
		return new PanneauSolaireSensorReport(this.getURI(), this.readings);
	}
}
