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
	}

	@Override
	public void setOn() {
		isOn = true;
	}

	@Override
	public void setOff() {
		isOn = false;
	}

	@Override
	public void setFreezerTemperatureCible(Double t) {
		freezer_temperature_cible = t;
	}

	@Override
	public void setFridgeTemperatureCible(Double t) {
		fridge_temperature_cible = t;
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
	
	public void tick() {
		if(isOn) {
			freezerStabilize();
			fridgeStabilize();
		}
	}

}
