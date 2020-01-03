package simulator.models.lavelinge;

import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import org.apache.commons.math3.random.RandomDataGenerator;
import app.util.ModeLaveLinge;
import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentStateAccessI;
import fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import simulator.events.lavelinge.SetEssorage;
import simulator.events.lavelinge.SetInternalTransition;
import simulator.events.lavelinge.SetLavage;
import simulator.events.lavelinge.SetLaveLingeVeille;
import simulator.events.lavelinge.SetRincage;
import simulator.events.lavelinge.SetSechage;
import simulator.events.lavelinge.SwitchLaveLingeOff;

@ModelExternalEvents(exported = { 
		SetEssorage.class,
		SetLavage.class,
		SetLaveLingeVeille.class,
		SetRincage.class,
		SetSechage.class,
		SwitchLaveLingeOff.class,
		SetInternalTransition.class
})

public class LaveLingePlanificationModel extends AtomicES_Model{
	private static final long serialVersionUID = 1L;
	public static final String URI = "LaveLingePlanificationModel";

	protected double initialDelay;
	protected double meanTimeBetweenUsages;
	protected double meanTimeExecuteTask;
	protected Class<?> nextEvent;
	protected final RandomDataGenerator rg;
	protected ModeLaveLinge etat_lavelinge;
	
	// delai dans lequel la planification d'un mode va se declencher
	protected double delai;
	
	protected ArrayList<ModeLaveLinge> planification_etats;
	
	protected EmbeddingComponentStateAccessI componentRef;
	
	
	public LaveLingePlanificationModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		this.rg = new RandomDataGenerator();
		this.planification_etats = new ArrayList<ModeLaveLinge>();
		this.setLogger(new StandardLogger());
	}
	
	@Override
	public void initialiseState(Time initialTime) {
		this.initialDelay = 10.0;
		this.meanTimeBetweenUsages = 10.0;
		this.meanTimeExecuteTask = 300.0;
		
		this.delai = 0.0;

		this.rg.reSeedSecure();

		super.initialiseState(initialTime);
		
		this.etat_lavelinge = ModeLaveLinge.OFF;
		Duration d1 = new Duration(this.initialDelay, this.getSimulatedTimeUnit());
		Duration d2 = new Duration(2.0 * this.meanTimeBetweenUsages * this.rg.nextBeta(1.75, 1.75),
				this.getSimulatedTimeUnit());
		Time t = this.getCurrentStateTime().add(d1).add(d2);
		this.scheduleEvent(new SetLaveLingeVeille(t));

		this.nextTimeAdvance = this.timeAdvance();
		this.timeOfNextEvent = this.getCurrentStateTime().add(this.nextTimeAdvance);

		try {
			// this.setDebugLevel(1) ;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	@Override
	public void setSimulationRunParameters(Map<String, Object> simParams) throws Exception {
		this.componentRef = (EmbeddingComponentStateAccessI) simParams.get(LaveLingeModel.URI + " : " + LaveLingeModel.COMPONENT_REF);
	}
	
	@Override
	public Duration timeAdvance() {

		Duration d = super.timeAdvance();
		this.logMessage("LaveLingePlanificationModel::timeAdvance() 1 " + d + " " + this.eventListAsString());
		return d;
	}

	@Override
	public Vector<EventI> output() {

		assert !this.eventList.isEmpty();

		Vector<EventI> ret = super.output();

		assert ret.size() == 1;
		this.nextEvent = ret.get(0).getClass();

		this.logMessage("LaveLingePlanificationModel::output() " + this.nextEvent.getCanonicalName());
		return ret;
	}
	
	@Override
	public void userDefinedInternalTransition(Duration elapsedTime) {
		try {
			@SuppressWarnings("unchecked")
			ArrayList<ModeLaveLinge> planification = (ArrayList<ModeLaveLinge>) componentRef.getEmbeddingComponentStateValue(LaveLingeModel.URI + " : planification");
			
			if(planification_etats.isEmpty() && planification != null) {
				this.delai = (double) componentRef.getEmbeddingComponentStateValue(LaveLingeModel.URI + " : delai");
				planification_etats = planification;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Duration d;
		int i = 1;
		for (ModeLaveLinge mode : planification_etats) {
			d = new Duration(2.0 * (this.delai + this.meanTimeExecuteTask * i), this.getSimulatedTimeUnit());

			if(mode == ModeLaveLinge.LAVAGE)
				this.scheduleEvent(new SetLavage(this.getCurrentStateTime().add(d)));
			else if (mode == ModeLaveLinge.RINCAGE)
				this.scheduleEvent(new SetRincage(this.getCurrentStateTime().add(d)));
			else if (mode == ModeLaveLinge.ESSORAGE)
				this.scheduleEvent(new SetEssorage(this.getCurrentStateTime().add(d)));
			else if (mode == ModeLaveLinge.SECHAGE)
				this.scheduleEvent(new SetSechage(this.getCurrentStateTime().add(d)));
			else if (mode == ModeLaveLinge.VEILLE)
				this.scheduleEvent(new SetLaveLingeVeille(this.getCurrentStateTime().add(d)));
			else if (mode == ModeLaveLinge.OFF)
				this.scheduleEvent(new SwitchLaveLingeOff(this.getCurrentStateTime().add(d)));
		
			i++;
		}
		
		// event de mise en veille au cas ou le controleur n'aurait pas planifie la veille
		if(i > 1) {
			System.out.println("i : " + i);
			d = new Duration(2.0 * (this.delai + this.meanTimeExecuteTask * (i+1) ), this.getSimulatedTimeUnit());
			System.out.println(d);
			this.scheduleEvent(new SetLaveLingeVeille(this.getCurrentStateTime().add(d)));
		}
		
		// event de
		d = new Duration(2.0 * (this.delai + this.meanTimeExecuteTask * i), this.getSimulatedTimeUnit());
		this.scheduleEvent(new SetInternalTransition(this.getCurrentStateTime().add(d)));
		
		planification_etats = new ArrayList<>();
	}
}
