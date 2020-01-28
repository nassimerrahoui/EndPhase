package simulator.events.aspirateur;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulator.models.aspirateur.AspirateurUserModel;
import app.util.ModeAspirateur;

public class SetPerformanceReduiteSIL extends SetPerformanceReduite {
	
	private static final long serialVersionUID = 1L;

	public SetPerformanceReduiteSIL(Time timeOfOccurrence) {
		super(timeOfOccurrence);
	}

	@Override
	public String eventAsString() {
		return "Aspirateur::SetPerformanceReduiteSIL";
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
		assert model instanceof AspirateurUserModel;
		AspirateurUserModel m = (AspirateurUserModel)model;
		try {
			m.getComponentRef().setModeAspirateur(ModeAspirateur.PERFORMANCE_REDUITE);
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
}