package simulator.events.lavelinge;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulator.models.lavelinge.LaveLingeModel;

public class SetInternalTransition extends AbstractLaveLingeEvent {

	private static final long serialVersionUID = 1L;

	public SetInternalTransition(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
	}
	
	@Override
	public String eventAsString() {
		return "LaveLinge::NextInternalTransition";
	}

	@Override
	public boolean hasPriorityOver(EventI e) {
		return true;
	}

	@Override
	public void executeOn(AtomicModel model) {
		assert model instanceof LaveLingeModel;
	}

}
