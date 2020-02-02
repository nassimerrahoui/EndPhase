package simulator.models.lavelinge;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.random.RandomDataGenerator;

import app.components.LaveLinge;
import app.util.ModeLaveLinge;
import fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import simulator.events.lavelinge.SetEssorageSIL;
import simulator.events.lavelinge.SetInternalTransitionSIL;
import simulator.events.lavelinge.SetLavageSIL;
import simulator.events.lavelinge.SetLaveLingeVeilleSIL;
import simulator.events.lavelinge.SetRincageSIL;
import simulator.events.lavelinge.SetSechageSIL;
import simulator.events.lavelinge.SwitchLaveLingeOffSIL;

/**
 * @author Willy Nassim
 */

public class LaveLingePlanificationModel extends AtomicES_Model{
	private static final long serialVersionUID = 1L;
	public static final String URI = "LaveLingePlanificationModel";

	protected double initialDelay;
	protected double meanTimeBetweenUsages;
	protected double meanTimeExecuteTask;
	protected final RandomDataGenerator rg;
	
	/** etat du lave-linge */
	protected ModeLaveLinge etat_lavelinge;
	
	/** delai dans lequel la planification d'un mode va se declencher */
	protected double delai;
	
	protected ArrayList<ModeLaveLinge> planification_etats;
	
	protected LaveLinge componentRef;
	
	
	public LaveLingePlanificationModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		this.rg = new RandomDataGenerator();
		this.planification_etats = new ArrayList<ModeLaveLinge>();
		this.setLogger(new StandardLogger());
	}
	
	public LaveLinge getComponentRef() {
		return this.componentRef;
	}
	
	@Override
	public void initialiseState(Time initialTime) {
		this.initialDelay = 10.0;
		this.meanTimeBetweenUsages = 100.0;
		this.meanTimeExecuteTask = 20.0;
		this.delai = 0.0;
		this.rg.reSeedSecure();

		super.initialiseState(initialTime);
		
		this.etat_lavelinge = ModeLaveLinge.OFF;
		Duration d1 = new Duration(this.initialDelay, this.getSimulatedTimeUnit());
		Time t = this.getCurrentStateTime().add(d1);
		this.scheduleEvent(new SetLaveLingeVeilleSIL(t));

		this.nextTimeAdvance = this.timeAdvance();
		this.timeOfNextEvent = this.getCurrentStateTime().add(this.nextTimeAdvance);
	}
	
	
	@Override
	public void setSimulationRunParameters(Map<String, Object> simParams) throws Exception {
		this.componentRef = (LaveLinge) simParams.get(LaveLingeModel.URI + " : " + LaveLingeModel.COMPONENT_REF);
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
		
		// ici on recupere la liste de planification des etat pour notre cycle
		// chaque etat dans la liste sera planifie dans un delai donne
		// l'ordre dans lequel les etat ont ete ajoute dans la liste est important
		// car elle determine l'ordre du cycle
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
			Duration d2 = new Duration(this.meanTimeExecuteTask * i, this.getSimulatedTimeUnit());
			Time t = this.getCurrentStateTime().add(d2);

			if(mode == ModeLaveLinge.LAVAGE)
				this.scheduleEvent(new SetLavageSIL(t));
			else if (mode == ModeLaveLinge.RINCAGE)
				this.scheduleEvent(new SetRincageSIL(t));
			else if (mode == ModeLaveLinge.ESSORAGE)
				this.scheduleEvent(new SetEssorageSIL(t));
			else if (mode == ModeLaveLinge.SECHAGE)
				this.scheduleEvent(new SetSechageSIL(t));
			else if (mode == ModeLaveLinge.VEILLE)
				this.scheduleEvent(new SetLaveLingeVeilleSIL(t));
			else if (mode == ModeLaveLinge.OFF)
				this.scheduleEvent(new SwitchLaveLingeOffSIL(t));
		
			i++;
		}
		
		// event de mise en arret si aucune planification n'a ete faite
		if(i > 1) {
			d = new Duration((this.delai + this.meanTimeExecuteTask * (i+1) ), this.getSimulatedTimeUnit());
			this.scheduleEvent(new SetLaveLingeVeilleSIL(this.getCurrentStateTime().add(d)));
		}
		
		// event de transition si aucun etat est planifie
		d = new Duration((this.delai + this.meanTimeBetweenUsages * i), this.getSimulatedTimeUnit());
		this.scheduleEvent(new SetInternalTransitionSIL(this.getCurrentStateTime().add(d)));
		
		planification_etats = new ArrayList<>();
		
		super.userDefinedInternalTransition(elapsedTime);
	}
}
