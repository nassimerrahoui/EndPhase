package app.connectors;

import java.util.ArrayList;

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
	public void envoyerPlanificationCycle(ArrayList<ModeLaveLinge> planification, int heure, int minutes) throws Exception {
		((ILaveLinge) this.offering).planifierCycle(planification, heure, minutes);
	}

	@Override
	public void envoyerTemperature(TemperatureLaveLinge tl) throws Exception {
		((ILaveLinge) this.offering).setTemperature(tl);
	}
}
