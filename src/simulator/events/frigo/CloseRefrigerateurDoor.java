package simulator.events.frigo;

import app.util.ModeFrigo;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulator.models.frigo.FrigoModel;

/**
 * @author Willy Nassim
 */

public class CloseRefrigerateurDoor extends AbstractFrigoEvent {

	private static final long serialVersionUID = 1L;

	public CloseRefrigerateurDoor(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
	}

	@Override
	public String eventAsString() {
		return "Frigo::SetLightOff";
	}

	@Override
	public boolean hasPriorityOver(EventI e) {
		if (e instanceof SwitchFrigoOn || e instanceof OpenRefrigerateurDoor) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void executeOn(AtomicModel model) {
		assert model instanceof FrigoModel;

		FrigoModel m = (FrigoModel) model;
		if (m.getState() == ModeFrigo.LIGHT_ON) {
			m.setState(ModeFrigo.LIGHT_OFF);
		}
	}
}
