package simulator.models.frigo;

import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public class Temperature extends Event{

	private static final long serialVersionUID = 1L;

	public Temperature(Time timeOfOccurrence, double temperature){
		super(timeOfOccurrence, new Reading(temperature)) ;
		assert	timeOfOccurrence != null && temperature >= 0.0 ;
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
		return "Temperature(" + this.eventContentAsString() + ")";
	}

	@Override
	public String eventContentAsString() {
		return "time = " + this.getTimeOfOccurrence() + ", " + "temperature = " + ((Reading) this.getEventInformation()).value
				+ " °C";
	}

}
