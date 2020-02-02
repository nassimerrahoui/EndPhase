package simulator.events.lavelinge;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

/**
 * Evenement permettant au modele qui le recoit de passer sa transition interne
 * (non utilise actuelement)
 *
 * @author Willy Nassim
 *
 */
public class SetInternalTransitionSIL extends SetInternalTransition {

	private static final long serialVersionUID = 1L;

	public SetInternalTransitionSIL(Time timeOfOccurrence) {
		super(timeOfOccurrence);
	}
	
	@Override
	public String eventAsString() {
		return "LaveLinge::NextInternalTransitionSIL";
	}

	@Override
	public boolean hasPriorityOver(EventI e) {
		return true;
	}

	@Override
	public void executeOn(AtomicModel model) {
		//
	}

}
