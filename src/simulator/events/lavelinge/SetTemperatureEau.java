package simulator.events.lavelinge;

import app.util.ModeLaveLinge;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulator.models.lavelinge.LaveLingeModel;

/**
 * Evenement permettant d'ameliorer le modele du lave-linge (non utilise actuellement)
 *
 * @author Willy Nassim
 *
 */
public class SetTemperatureEau extends AbstractLaveLingeEvent {

	private static final long serialVersionUID = 1L;

	public SetTemperatureEau(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
	}
	
	@Override
	public String eventAsString() {
		return "LaveLinge::SwitchSetTemperature";
	}

	@Override
	public boolean hasPriorityOver(EventI e) {
		return true;
	}

	@Override
	public void executeOn(AtomicModel model) {
		assert model instanceof LaveLingeModel;
		((LaveLingeModel) model).setState(ModeLaveLinge.CHAUFFER_EAU);
	}

}
