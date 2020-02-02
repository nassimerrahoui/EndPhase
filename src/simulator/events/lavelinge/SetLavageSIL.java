package simulator.events.lavelinge;

import app.util.ModeLaveLinge;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulator.models.lavelinge.LaveLingePlanificationModel;

/**
 * @author Willy Nassim
 */

public class SetLavageSIL extends SetLavage {

	private static final long serialVersionUID = 1L;

	public SetLavageSIL(Time timeOfOccurrence) {
		super(timeOfOccurrence);
	}
	
	@Override
	public String eventAsString() {
		return "LaveLinge::SwitchSetLavageSIL";
	}

	@Override
	public boolean hasPriorityOver(EventI e) {
		return true;
	}

	@Override
	public void executeOn(AtomicModel model) {
		LaveLingePlanificationModel m = (LaveLingePlanificationModel)model;
		try {
			((LaveLingePlanificationModel) m).getComponentRef().setModeLaveLinge(ModeLaveLinge.LAVAGE);
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}

}
