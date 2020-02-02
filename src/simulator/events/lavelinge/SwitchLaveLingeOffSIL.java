package simulator.events.lavelinge;

import app.util.ModeLaveLinge;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulator.models.lavelinge.LaveLingePlanificationModel;

/**
 * @author Willy Nassim
 */

public class SwitchLaveLingeOffSIL extends SwitchLaveLingeOff {

	private static final long serialVersionUID = 1L;

	public SwitchLaveLingeOffSIL(Time timeOfOccurrence) {
		super(timeOfOccurrence);
	}

	@Override
	public String eventAsString() {
		return "LaveLinge::SwitchLaveLingeOffSIL";
	}

	@Override
	public boolean hasPriorityOver(EventI e) {
		return false;
	}

	@Override
	public void executeOn(AtomicModel model) {
		LaveLingePlanificationModel m = (LaveLingePlanificationModel)model;
		try {
			((LaveLingePlanificationModel) m).getComponentRef().setModeLaveLinge(ModeLaveLinge.OFF);
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}

}
