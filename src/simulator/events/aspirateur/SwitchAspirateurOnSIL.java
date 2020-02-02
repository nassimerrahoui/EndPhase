package simulator.events.aspirateur;

import app.util.ModeAspirateur;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulator.models.aspirateur.AspirateurUserModel;

/**
 * @author Willy Nassim
 */

public class SwitchAspirateurOnSIL extends SwitchAspirateurOn {

	private static final long serialVersionUID = 1L;

	public SwitchAspirateurOnSIL(Time timeOfOccurrence) {
		super(timeOfOccurrence);
	}
	
	@Override
	public String eventAsString() {
		return "Aspirateur::SwitchAspirateurOnSIL";
	}

	@Override
	public boolean hasPriorityOver(EventI e) {
		return true;
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