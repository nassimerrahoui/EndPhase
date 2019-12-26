package simulator.events;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulator.models.AspirateurModel;
import app.util.ModeAspirateur;

public class SwitchOff extends AbstractAspirateurEvent {

	private static final long serialVersionUID = 1L;

	public SwitchOff(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
	}

	@Override
	public String eventAsString() {
		return "Aspirateur::SwitchOff";
	}

	@Override
	public boolean hasPriorityOver(EventI e) {
		return false;
	}

	@Override
	public void executeOn(AtomicModel model) {
		assert model instanceof AspirateurModel;
		((AspirateurModel) model).setState(ModeAspirateur.OFF);
	}
}