package simulator.events.controleur;

import app.util.ModeLaveLinge;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulator.models.lavelinge.LaveLingeModel;

public class SwitchLaveLingeOff extends AbstractLaveLingeEvent {

	private static final long serialVersionUID = 1L;

	public SwitchLaveLingeOff(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
	}

	@Override
	public String eventAsString() {
		return "LaveLinge::SwitchLaveLingeOff";
	}

	@Override
	public boolean hasPriorityOver(EventI e) {
		return false;
	}

	@Override
	public void executeOn(AtomicModel model) {
		assert model instanceof LaveLingeModel;
		((LaveLingeModel) model).setState(ModeLaveLinge.OFF);
	}

}
