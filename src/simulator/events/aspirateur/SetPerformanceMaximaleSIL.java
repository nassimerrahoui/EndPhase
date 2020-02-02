package simulator.events.aspirateur;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulator.models.aspirateur.AspirateurUserModel;
import app.util.ModeAspirateur;

/**
 * @author Willy Nassim
 */

public class SetPerformanceMaximaleSIL extends SetPerformanceMaximale {

	private static final long serialVersionUID = 1L;

	public SetPerformanceMaximaleSIL(Time timeOfOccurrence) {
		super(timeOfOccurrence);
	}

	@Override
	public String eventAsString() {
		return "Aspirateur::SetPerformanceMaximaleSIL";
	}

	@Override
	public void executeOn(AtomicModel model) {
		assert model instanceof AspirateurUserModel;
		AspirateurUserModel m = (AspirateurUserModel)model;
		
		try {
			m.getComponentRef().setModeAspirateur(ModeAspirateur.PERFORMANCE_MAXIMALE);
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
}