package simulator.models;

import java.util.concurrent.TimeUnit;
import org.apache.commons.math3.random.RandomDataGenerator;
import app.util.ModeAspirateur;
import fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import simulator.events.SetPerformanceMaximale;
import simulator.events.SetPerformanceReduite;
import simulator.events.SwitchOff;
import simulator.events.SwitchOn;

import java.util.Vector;

@ModelExternalEvents(exported = { 
		SwitchOn.class, 
		SwitchOff.class, 
		SetPerformanceReduite.class, 
		SetPerformanceMaximale.class })

public class AspirateurUserModel extends AtomicES_Model {

	private static final long serialVersionUID = 1L;
	public static final String URI = "AspirateurUserModel";

	protected double initialDelay;
	protected double interdayDelay;
	protected double meanTimeBetweenUsages;
	protected double meanTimeAtPerformanceMaximale;
	protected double meanTimeAtPerformanceReduite;
	protected Class<?> nextEvent;
	protected final RandomDataGenerator rg;
	protected ModeAspirateur etat_aspirateur;

	public AspirateurUserModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);

		this.rg = new RandomDataGenerator();

		this.setLogger(new StandardLogger());
	}

	@Override
	public void initialiseState(Time initialTime) {
		this.initialDelay = 10.0;
		this.interdayDelay = 100.0;
		this.meanTimeBetweenUsages = 1.0;
		this.meanTimeAtPerformanceMaximale = 10.0;
		this.meanTimeAtPerformanceReduite = 10.0;
		this.etat_aspirateur = ModeAspirateur.OFF;

		this.rg.reSeedSecure();

		super.initialiseState(initialTime);

		Duration d1 = new Duration(this.initialDelay, this.getSimulatedTimeUnit());
		Duration d2 = new Duration(2.0 * this.meanTimeBetweenUsages * this.rg.nextBeta(1.75, 1.75),
				this.getSimulatedTimeUnit());
		Time t = this.getCurrentStateTime().add(d1).add(d2);
		this.scheduleEvent(new SwitchOn(t));

		this.nextTimeAdvance = this.timeAdvance();
		this.timeOfNextEvent = this.getCurrentStateTime().add(this.nextTimeAdvance);

		try {
			// this.setDebugLevel(1) ;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Duration timeAdvance() {

		Duration d = super.timeAdvance();
		this.logMessage("AspirateurUserModel::timeAdvance() 1 " + d + " " + this.eventListAsString());
		return d;
	}

	@Override
	public Vector<EventI> output() {

		assert !this.eventList.isEmpty();

		Vector<EventI> ret = super.output();

		assert ret.size() == 1;
		this.nextEvent = ret.get(0).getClass();

		this.logMessage("AspirateurUserModel::output() " + this.nextEvent.getCanonicalName());
		return ret;
	}

	@Override
	public void userDefinedInternalTransition(Duration elapsedTime) {

		Duration d;
		if (this.nextEvent.equals(SwitchOn.class)) {

			d = new Duration(2.0 * this.rg.nextBeta(1.75, 1.75), this.getSimulatedTimeUnit());
			Time t = this.getCurrentStateTime().add(d);
			this.scheduleEvent(new SetPerformanceMaximale(t));

			d = new Duration(this.interdayDelay, this.getSimulatedTimeUnit());
			this.scheduleEvent(new SwitchOn(this.getCurrentStateTime().add(d)));
			
		} else if (this.nextEvent.equals(SetPerformanceMaximale.class)) {

			d = new Duration(2.0 * this.meanTimeAtPerformanceMaximale * this.rg.nextBeta(1.75, 1.75), this.getSimulatedTimeUnit());
			this.scheduleEvent(new SetPerformanceReduite(this.getCurrentStateTime().add(d)));
		} else if (this.nextEvent.equals(SetPerformanceReduite.class)) {

			d = new Duration(2.0 * this.meanTimeAtPerformanceReduite * this.rg.nextBeta(1.75, 1.75), this.getSimulatedTimeUnit());
			this.scheduleEvent(new SwitchOff(this.getCurrentStateTime().add(d)));
		}
	}
}