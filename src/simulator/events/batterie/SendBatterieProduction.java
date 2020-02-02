package simulator.events.batterie;

import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

/**
 * @author Willy Nassim
 */

public class SendBatterieProduction extends Event{
	private static final long serialVersionUID = 1L;

	public SendBatterieProduction(Time timeOfOccurrence, double consommation){
		super(timeOfOccurrence, new Reading(consommation)) ;
		assert	timeOfOccurrence != null && consommation >= 0.0 ;
	}
	
	public static class Reading implements EventInformationI {
		private static final long serialVersionUID = 1L;
		public final double value;

		public Reading(double value) {
			super();
			this.value = value;
		}
	}
	
	@Override
	public String eventAsString() {
		return "SendBatterieProduction(" + this.eventContentAsString() + ")";
	}

	@Override
	public String eventContentAsString() {
		return "temps = " + this.getTimeOfOccurrence() + ", " 
				+ "batterie production = " + ((Reading) this.getEventInformation()).value
				+ " w";
	}
}
