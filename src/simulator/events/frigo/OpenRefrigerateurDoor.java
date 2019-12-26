package simulator.events.frigo;

import app.util.ModeFrigo;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulator.models.frigo.FrigoModel;

public class OpenRefrigerateurDoor extends AbstractFrigoEvent{
	private static final long serialVersionUID = 1L;

	public OpenRefrigerateurDoor(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
	}

	@Override
	public String eventAsString() {
		return "Frigo::SetLightOn";
	}

	@Override
	public boolean hasPriorityOver(EventI e) {
		if (e instanceof SwitchFrigoOn || e instanceof CloseRefrigerateurDoor) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void executeOn(AtomicModel model) {
		assert model instanceof FrigoModel;

		FrigoModel m = (FrigoModel) model;
		if (m.getState() == ModeFrigo.LIGHT_OFF) {
			m.setState(ModeFrigo.LIGHT_ON);
		}
	}
}
