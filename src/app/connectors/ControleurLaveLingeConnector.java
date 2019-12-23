package app.connectors;

import app.interfaces.appareil.ILaveLinge;
import app.interfaces.controleur.IControleLaveLinge;
import app.util.ModeLaveLinge;
import app.util.TemperatureLaveLinge;
import fr.sorbonne_u.components.connectors.AbstractConnector;

public class ControleurLaveLingeConnector extends AbstractConnector implements IControleLaveLinge {

	@Override
	public void envoyerModeLaveLinge(ModeLaveLinge etat) throws Exception {
		((ILaveLinge) this.offering).setModeLaveLinge(etat);
	}

	@Override
	public void envoyerPlanificationCycle(int heure, int minutes) throws Exception {
		((ILaveLinge) this.offering).planifierCycle(heure, minutes);
	}

	@Override
	public void envoyerPlanificationMode(ModeLaveLinge ml, int heure, int minutes) throws Exception {
		((ILaveLinge) this.offering).planifierMode(ml, heure, minutes);
	}

	@Override
	public void envoyerTemperature(TemperatureLaveLinge tl) throws Exception {
		((ILaveLinge) this.offering).setTemperature(tl);
	}
}
