package simulator.events.aspirateur;

import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

/**
 * @author Willy Nassim
 */

public class AbstractAspirateurEvent extends ES_Event {

	private static final long serialVersionUID = 1L;

	public AbstractAspirateurEvent(Time timeOfOccurrence, EventInformationI content) {
		super(timeOfOccurrence, content);
	}
}