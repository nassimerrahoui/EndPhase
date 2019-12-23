package simulator.events;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulator.models.OrdinateurModel;
import app.util.ModeOrdinateur;

public class SwitchOff extends AbstractOrdinateurEvent {

	private static final long serialVersionUID = 1L;

	public SwitchOff(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
	}

	@Override
	public String eventAsString() {
		return "Ordinateur::SwitchOff";
	}

	@Override
	public boolean hasPriorityOver(EventI e) {
		return false;
	}

	@Override
	public void executeOn(AtomicModel model) {
		assert model instanceof OrdinateurModel;
		((OrdinateurModel) model).setState(ModeOrdinateur.OFF);
	}
}