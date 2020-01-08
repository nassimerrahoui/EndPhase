package simulator.events.batterie;

import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public class BatterieLevel extends Event {
	
	private static final long serialVersionUID = 1L;
	
	public static class Reading implements EventInformationI {
		private static final long serialVersionUID = 1L;
		public final double value;

		public Reading(double value) {
			super();
			this.value = value;
		}
	}
	
	
	public BatterieLevel(Time timeOfOccurrence, double batteryLevel){
		super(timeOfOccurrence, new Reading(batteryLevel)) ;
		assert	timeOfOccurrence != null && batteryLevel >= 0.0 ;
	}

	@Override
	public String eventAsString() {
		return "BatterieLevel(" + this.eventContentAsString() + ")";
	}

	@Override
	public String eventContentAsString() {
		return "time = " + this.getTimeOfOccurrence() + ", " + "level = " + ((Reading) this.getEventInformation()).value
				+ " w";
	}
	
}
