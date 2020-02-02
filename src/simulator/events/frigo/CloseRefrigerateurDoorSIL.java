package simulator.events.frigo;

import app.util.ModeFrigo;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulator.models.frigo.FrigoUserModel;

/**
 * @author Willy Nassim
 */

public class CloseRefrigerateurDoorSIL extends CloseRefrigerateurDoor {

	private static final long serialVersionUID = 1L;

	public CloseRefrigerateurDoorSIL(Time timeOfOccurrence) {
		super(timeOfOccurrence);
	}

	@Override
	public String eventAsString() {
		return "Frigo::SetLightOffSIL";
	}

	@Override
	public boolean hasPriorityOver(EventI e) {
		if (e instanceof SwitchFrigoOnSIL || e instanceof OpenRefrigerateurDoorSIL) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void executeOn(AtomicModel model) {
		assert model instanceof FrigoUserModel;
		FrigoUserModel m = (FrigoUserModel)model;
		try {
			m.getComponentRef().setModeFrigo(ModeFrigo.LIGHT_OFF);
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
}
