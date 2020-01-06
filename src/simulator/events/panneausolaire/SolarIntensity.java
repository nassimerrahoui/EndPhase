package simulator.events.panneausolaire;

import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public class SolarIntensity extends Event {

	private static final long serialVersionUID = 1L;

	public SolarIntensity(Time timeOfOccurrence, double intensity){
		super(timeOfOccurrence, new Reading(intensity)) ;
		assert	timeOfOccurrence != null && intensity >= 0.0 ;
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
		return "SolarIntensity(" + this.eventContentAsString() + ")";
	}

	@Override
	public String eventContentAsString() {
		return "temps = " + this.getTimeOfOccurrence() + ", " 
				+ "solarIntensity = " + ((Reading) this.getEventInformation()).value
				+ " KWC";
	}

}
