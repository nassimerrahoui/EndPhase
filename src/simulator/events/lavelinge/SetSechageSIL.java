package simulator.events.lavelinge;

import app.util.ModeLaveLinge;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulator.models.lavelinge.LaveLingePlanificationModel;

public class SetSechageSIL extends SetSechage {

	private static final long serialVersionUID = 1L;

	public SetSechageSIL(Time timeOfOccurrence) {
		super(timeOfOccurrence);
	}
	
	@Override
	public String eventAsString() {
		return "LaveLinge::SwitchSetSechageSIL";
	}

	@Override
	public boolean hasPriorityOver(EventI e) {
		return true;
	}

	@Override
	public void executeOn(AtomicModel model) {
		LaveLingePlanificationModel m = (LaveLingePlanificationModel)model;
		try {
			((LaveLingePlanificationModel) m).getComponentRef().setModeLaveLinge(ModeLaveLinge.SECHAGE);
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}

}
