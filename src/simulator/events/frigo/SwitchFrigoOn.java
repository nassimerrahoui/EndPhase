package simulator.events.frigo;

import app.util.ModeFrigo;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulator.models.frigo.FrigoModel;

/**
 * @author Willy Nassim
 */

public class SwitchFrigoOn extends AbstractFrigoEvent {

	private static final long serialVersionUID = 1L;

	public SwitchFrigoOn(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
	}
	
	@Override
	public String eventAsString() {
		return "Frigo::SwitchFrigoOn";
	}

	@Override
	public boolean hasPriorityOver(EventI e) {
		return true;
	}

	@Override
	public void executeOn(AtomicModel model) {
		assert model instanceof FrigoModel;
		((FrigoModel) model).setState(ModeFrigo.LIGHT_OFF);
	}

}
