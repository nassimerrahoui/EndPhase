package simulator.events.panneausolaire;

import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public class SendPanneauSolaireProduction extends Event{

	private static final long serialVersionUID = 1L;

	public SendPanneauSolaireProduction(Time timeOfOccurrence, double consommation){
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
		return "SendPanneauSolaireProduction(" + this.eventContentAsString() + ")";
	}

	@Override
	public String eventContentAsString() {
		return "temps = " + this.getTimeOfOccurrence() + ", " 
				+ "panneau solaire production = " + ((Reading) this.getEventInformation()).value
				+ " w";
	}
}
