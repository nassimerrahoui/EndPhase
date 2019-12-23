package simulator.events;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulator.models.OrdinateurModel;
import app.util.ModeOrdinateur;

public class SetPerformanceReduite extends AbstractOrdinateurEvent {
	
	private static final long serialVersionUID = 1L;

	public SetPerformanceReduite(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
	}

	@Override
	public String eventAsString() {
		return "Ordinateur::SetPerformanceReduite";
	}

	@Override
	public boolean hasPriorityOver(EventI e) {
		if (e instanceof SwitchOn || e instanceof SetPerformanceMaximale) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void executeOn(AtomicModel model) {
		assert model instanceof OrdinateurModel;

		OrdinateurModel m = (OrdinateurModel) model;
		if (m.getState() == ModeOrdinateur.PERFORMANCE_MAXIMALE) {
			m.setState(ModeOrdinateur.PERFORMANCE_REDUITE);
		}
	}
}