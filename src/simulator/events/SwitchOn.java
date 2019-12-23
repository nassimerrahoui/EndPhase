package simulator.events;

import app.util.ModeOrdinateur;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulator.models.OrdinateurModel;

public class SwitchOn extends AbstractOrdinateurEvent {

	private static final long serialVersionUID = 1L;

	public SwitchOn(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
	}
	
	@Override
	public String eventAsString() {
		return "Ordinateur::SwitchOn";
	}

	@Override
	public boolean hasPriorityOver(EventI e) {
		return true;
	}

	@Override
	public void executeOn(AtomicModel model) {
		assert model instanceof OrdinateurModel;
		((OrdinateurModel) model).setState(ModeOrdinateur.PERFORMANCE_REDUITE);
	}
}