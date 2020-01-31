package simulator.events.lavelinge;

import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public class SendLaveLingeConsommation extends Event {

	private static final long serialVersionUID = 1L;

	public SendLaveLingeConsommation(Time timeOfOccurrence, double consommation){
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
		return "SendLaveLingeConsommation(" + this.eventContentAsString() + ")";
	}

	@Override
	public String eventContentAsString() {
		return "temps = " + this.getTimeOfOccurrence() + ", " 
				+ "lave-linge consommation = " + ((Reading) this.getEventInformation()).value
				+ " w";
	}

}
