package simulator.events.lavelinge;

import app.util.ModeLaveLinge;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulator.models.lavelinge.LaveLingeModel;

/**
 * @author Willy Nassim
 */

public class SetLaveLingeVeille extends AbstractLaveLingeEvent {

	private static final long serialVersionUID = 1L;

	public SetLaveLingeVeille(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
	}
	
	@Override
	public String eventAsString() {
		return "LaveLinge::SwitchLaveLingeVeille";
	}

	@Override
	public boolean hasPriorityOver(EventI e) {
		return true;
	}

	@Override
	public void executeOn(AtomicModel model) {
		assert model instanceof LaveLingeModel;
		((LaveLingeModel) model).setState(ModeLaveLinge.VEILLE);
	}

}
