package simulator.events.aspirateur;

import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public class SendAspirateurConsommation extends Event{

	private static final long serialVersionUID = 1L;

	public SendAspirateurConsommation(Time timeOfOccurrence, double consommation){
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
		return "SendAspirateurConsommation(" + this.eventContentAsString() + ")";
	}

	@Override
	public String eventContentAsString() {
		return "temps = " + this.getTimeOfOccurrence() + ", " 
				+ "aspirateur consommation = " + ((Reading) this.getEventInformation()).value
				+ " w";
	}

}
