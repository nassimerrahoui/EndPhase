package app.components;

import app.interfaces.IFrigo;
import fr.sorbonne_u.components.AbstractComponent;

public class Frigo extends AbstractComponent implements IFrigo {
	
	Boolean isOn;
	Double freezer_temperature;
	Double freezer_temperature_cible;
	Double fridge_temperature;
	Double fridge_temperature_cible;
	
	protected Frigo(int nbThreads, int nbSchedulableThreads) {
		super(nbThreads, nbSchedulableThreads);
		createNewExecutorService("controleur_message", 10, true);
		createNewExecutorService("compteur_message",10, true);
		this.toggleTracing();
	}
	
	private void freezerStabilize() {
		if(freezer_temperature_cible - freezer_temperature > 2) {
			freezer_temperature += 0.2;
		} else if (freezer_temperature_cible - freezer_temperature < -2) {
			freezer_temperature -= 0.2;
		}
	}
	
	private void fridgeStabilize() {
		if(fridge_temperature_cible - fridge_temperature > 2) {
			fridge_temperature += 0.2;
		} else if (fridge_temperature_cible - fridge_temperature < -2) {
			fridge_temperature -= 0.2;
		}
	}
	
	/** TODO **/
	public void tick() {
		if(isOn) {
			freezerStabilize();
			fridgeStabilize();
		}
	}

	@Override
	public void recevoirMessage(DataI d) throws Exception {
		this.traceMessage("test");
	}

}
