package simulator.models.aspirateur;

import java.util.concurrent.TimeUnit;
import org.apache.commons.math3.random.RandomDataGenerator;
import app.components.Aspirateur;
import app.util.ModeAspirateur;
import fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import simulator.events.aspirateur.SetPerformanceMaximaleSIL;
import simulator.events.aspirateur.SetPerformanceReduiteSIL;
import simulator.events.aspirateur.SwitchAspirateurOffSIL;
import simulator.events.aspirateur.SwitchAspirateurOnSIL;

import java.util.ArrayList;
import java.util.Map;


public class AspirateurUserModel extends AtomicES_Model {

	private static final long serialVersionUID = 1L;
	public static final String URI = "AspirateurUserModel";

	protected double initialDelay;
	protected double interdayDelay;
	protected double meanTimeBetweenUsages;
	protected double meanTimeAtPerformanceMaximale;
	protected double meanTimeAtPerformanceReduite;
	protected double meanTimeOff;
	protected Class<?> nextEvent;
	protected final RandomDataGenerator rg;
	protected ModeAspirateur etat_aspirateur;
	protected Aspirateur componentRef;

	public AspirateurUserModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);

		this.rg = new RandomDataGenerator();

		this.setLogger(new StandardLogger());
	}
	
	public Aspirateur getComponentRef() {
		return this.componentRef;
	}
	
	@Override
	public void setSimulationRunParameters(Map<String, Object> simParams) throws Exception {
		this.componentRef = (Aspirateur) simParams.get(AspirateurModel.URI + " : " + AspirateurModel.COMPONENT_REF);
	}

	@Override
	public void initialiseState(Time initialTime) {
		this.initialDelay = 10.0;
		this.interdayDelay = 360 * 8;
		this.meanTimeBetweenUsages = 10.0;
		this.meanTimeAtPerformanceMaximale = 120.0;
		this.meanTimeAtPerformanceReduite = 30.0;
		this.meanTimeOff = 600.0;
		this.etat_aspirateur = ModeAspirateur.OFF;

		this.rg.reSeedSecure();

		super.initialiseState(initialTime);

		Duration d1 = new Duration(this.initialDelay, this.getSimulatedTimeUnit());
		Duration d2 = new Duration(2.0 * this.meanTimeBetweenUsages * this.rg.nextBeta(1.75, 1.75),
				this.getSimulatedTimeUnit());
		Time t = this.getCurrentStateTime().add(d1).add(d2);
		this.scheduleEvent(new SwitchAspirateurOnSIL(t));

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
		return d;
	}

	@Override
	public ArrayList<EventI> output() {
		return null;
	}

	@Override
	public void userDefinedInternalTransition(Duration elapsedTime) {
		System.out.println("Internal Transition Aspi User");
		Duration d;

		//assert this.eventList.size() >= 1 ;
		this.nextEvent = this.eventList.peek().getClass() ;
		
		if (this.nextEvent.equals(SwitchAspirateurOnSIL.class)) {
			
			d = new Duration(2 * this.rg.nextBeta(1.75, 1.75), this.getSimulatedTimeUnit());
			Time t = this.getCurrentStateTime().add(d);
			this.scheduleEvent(new SetPerformanceReduiteSIL(t));
			
		} else if (this.nextEvent.equals(SetPerformanceMaximaleSIL.class)) {
			d = new Duration(2 * this.meanTimeAtPerformanceMaximale * this.rg.nextBeta(1.75, 1.75), this.getSimulatedTimeUnit());
			this.scheduleEvent(new SwitchAspirateurOffSIL(this.getCurrentStateTime().add(d)));
			
		} else if (this.nextEvent.equals(SetPerformanceReduiteSIL.class)) {
			d = new Duration(2 * this.meanTimeAtPerformanceReduite * this.rg.nextBeta(1.75, 1.75), this.getSimulatedTimeUnit());
			this.scheduleEvent(new SetPerformanceMaximaleSIL(this.getCurrentStateTime().add(d)));
		
		} else if (this.nextEvent.equals(SwitchAspirateurOffSIL.class)) {
			d = new Duration(2 * this.meanTimeOff * this.rg.nextBeta(1.75, 1.75), this.getSimulatedTimeUnit());
			this.scheduleEvent(new SwitchAspirateurOnSIL(this.getCurrentStateTime().add(d)));
		}		
	}
}