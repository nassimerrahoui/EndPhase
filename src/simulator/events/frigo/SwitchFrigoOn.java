package simulator.events.frigo;

import app.util.ModeFrigo;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulator.models.frigo.FrigoModel;

public class SwitchFrigoOn extends AbstractFrigoEvent{

	private static final long serialVersionUID = 1L;

	public SwitchFrigoOn(Time timeOfOccurrence, EventInformationI content) {
		super(timeOfOccurrence, content);
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
