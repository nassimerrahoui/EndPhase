package simulator.events.frigo;

import app.util.ModeFrigo;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulator.models.frigo.FrigoModel;

/**
 * @author Willy Nassim
 */

public class SwitchFrigoOff extends AbstractFrigoEvent {

	private static final long serialVersionUID = 1L;

	public SwitchFrigoOff(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
	}
	
	@Override
	public String eventAsString() {
		return "Frigo::SwitchFrigoOff";
	}

	@Override
	public boolean hasPriorityOver(EventI e) {
		return false;
	}

	@Override
	public void executeOn(AtomicModel model) {
		assert model instanceof FrigoModel;
		((FrigoModel) model).setState(ModeFrigo.OFF);
	}

}
