package simulator.events.aspirateur;

import app.util.ModeAspirateur;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulator.models.aspirateur.AspirateurModel;

public class SwitchAspirateurOn extends AbstractAspirateurEvent {

	private static final long serialVersionUID = 1L;

	public SwitchAspirateurOn(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
	}
	
	@Override
	public String eventAsString() {
		return "Aspirateur::SwitchAspirateurOn";
	}

	@Override
	public boolean hasPriorityOver(EventI e) {
		return true;
	}

	@Override
	public void executeOn(AtomicModel model) {
		assert model instanceof AspirateurModel;
		((AspirateurModel) model).setState(ModeAspirateur.PERFORMANCE_REDUITE);
	}
}