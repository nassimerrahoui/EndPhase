package simulator.events.aspirateur;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulator.models.aspirateur.AspirateurModel;
import app.util.ModeAspirateur;

/**
 * @author Willy Nassim
 */

public class SetPerformanceReduite extends AbstractAspirateurEvent {
	
	private static final long serialVersionUID = 1L;

	public SetPerformanceReduite(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
	}

	@Override
	public String eventAsString() {
		return "Aspirateur::SetPerformanceReduite";
	}

	@Override
	public boolean hasPriorityOver(EventI e) {
		if (e instanceof SwitchAspirateurOn || e instanceof SetPerformanceMaximale) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void executeOn(AtomicModel model) {
		assert model instanceof AspirateurModel;

		((AspirateurModel) model).setState(ModeAspirateur.PERFORMANCE_REDUITE);
	}
}