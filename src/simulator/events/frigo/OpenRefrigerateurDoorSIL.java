package simulator.events.frigo;

import app.util.ModeFrigo;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulator.models.frigo.FrigoUserModel;

/**
 * @author Willy Nassim
 */

public class OpenRefrigerateurDoorSIL extends OpenRefrigerateurDoor {
	private static final long serialVersionUID = 1L;

	public OpenRefrigerateurDoorSIL(Time timeOfOccurrence) {
		super(timeOfOccurrence);
	}

	@Override
	public String eventAsString() {
		return "Frigo::SetLightOnSIL";
	}

	@Override
	public boolean hasPriorityOver(EventI e) {
		if (e instanceof SwitchFrigoOnSIL || e instanceof CloseRefrigerateurDoorSIL) {
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
			m.getComponentRef().setModeFrigo(ModeFrigo.LIGHT_ON);
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
}
