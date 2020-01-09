package simulator.models.frigo;

import java.util.Vector;

import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.random.RandomDataGenerator;

import app.util.ModeFrigo;
import fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import simulator.events.frigo.CloseRefrigerateurDoor;
import simulator.events.frigo.OpenRefrigerateurDoor;
import simulator.events.frigo.SwitchFrigoOff;
import simulator.events.frigo.SwitchFrigoOn;

@ModelExternalEvents(exported = { 
		SwitchFrigoOn.class, 
		SwitchFrigoOff.class, 
		CloseRefrigerateurDoor.class, 
		OpenRefrigerateurDoor.class })

public class FrigoUserModel extends AtomicES_Model{

	private static final long serialVersionUID = 1L;
	public static final String URI = "FrigoUserModel";

	protected double initialDelay;
	protected double interdayDelay;
	protected double meanTimeBetweenUsages;
	protected double meanTimeAtOpenDoor;
	protected double meanTimeAtCloseDoor;
	protected Class<?> nextEvent;
	protected final RandomDataGenerator rg;
	protected ModeFrigo etat_frigo;
	
	
	public FrigoUserModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		this.rg = new RandomDataGenerator();

		this.setLogger(new StandardLogger());
	}
	
	@Override
	public void initialiseState(Time initialTime) {
		this.initialDelay = 10.0;
		this.interdayDelay = 360 * 8;
		this.meanTimeBetweenUsages = 10.0;
		this.meanTimeAtOpenDoor = 2.0;
		this.meanTimeAtCloseDoor = 300.0;

		this.rg.reSeedSecure();

		super.initialiseState(initialTime);
		
		this.etat_frigo = ModeFrigo.OFF;
		Duration d1 = new Duration(this.initialDelay, this.getSimulatedTimeUnit());
		Duration d2 = new Duration(2.0 * this.meanTimeBetweenUsages * this.rg.nextBeta(1.75, 1.75),
				this.getSimulatedTimeUnit());
		Time t = this.getCurrentStateTime().add(d1).add(d2);
		this.scheduleEvent(new SwitchFrigoOn(t));

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
		this.logMessage("FrigoUserModel::timeAdvance() 1 " + d + " " + this.eventListAsString());
		return d;
	}

	@Override
	public Vector<EventI> output() {

		assert !this.eventList.isEmpty();

		Vector<EventI> ret = super.output();

		assert ret.size() == 1;
		this.nextEvent = ret.get(0).getClass();

		this.logMessage("FrigoUserModel::output() " + this.nextEvent.getCanonicalName());
		return ret;
	}
	
	@Override
	public void userDefinedInternalTransition(Duration elapsedTime) {

		Duration d;
		if (this.nextEvent.equals(SwitchFrigoOn.class)) {

			d = new Duration(2.0 * this.rg.nextBeta(1.75, 1.75), this.getSimulatedTimeUnit());
			Time t = this.getCurrentStateTime().add(d);
			this.scheduleEvent(new OpenRefrigerateurDoor(t));
			
			d = new Duration(this.interdayDelay, this.getSimulatedTimeUnit());
			this.scheduleEvent(new SwitchFrigoOn(this.getCurrentStateTime().add(d)));
			
			
		} else if (this.nextEvent.equals(OpenRefrigerateurDoor.class)) {

			d = new Duration(2.0 * this.meanTimeAtOpenDoor * this.rg.nextBeta(1.75, 1.75), this.getSimulatedTimeUnit());
			this.scheduleEvent(new CloseRefrigerateurDoor(this.getCurrentStateTime().add(d)));
			
		} else if (this.nextEvent.equals(CloseRefrigerateurDoor.class)) {

			d = new Duration(2.0 * this.meanTimeAtCloseDoor * this.rg.nextBeta(1.75, 1.75), this.getSimulatedTimeUnit());
			this.scheduleEvent(new OpenRefrigerateurDoor(this.getCurrentStateTime().add(d)));
		}
	}
}
