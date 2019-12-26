package simulator.events;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulator.models.AspirateurModel;
import app.util.ModeAspirateur;

public class SetPerformanceMaximale extends AbstractAspirateurEvent {

	private static final long serialVersionUID = 1L;

	public SetPerformanceMaximale(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
	}

	@Override
	public String eventAsString() {
		return "Aspirateur::SetPerformanceMaximale";
	}

	@Override
	public boolean hasPriorityOver(EventI e) {
		if (e instanceof SwitchOn || e instanceof SetPerformanceReduite) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void executeOn(AtomicModel model) {
		assert model instanceof AspirateurModel;

		AspirateurModel m = (AspirateurModel) model;
		if (m.getState() == ModeAspirateur.PERFORMANCE_REDUITE) {
			m.setState(ModeAspirateur.PERFORMANCE_MAXIMALE);
		}
	}
}