package app.connectors;

import app.components.LaveLinge;
import app.interfaces.controleur.IControleLaveLinge;
import app.util.EtatAppareil;
import app.util.ModeLaveLinge;
import app.util.TemperatureLaveLinge;
import fr.sorbonne_u.components.connectors.AbstractConnector;

public class ControleurLaveLingeConnector extends AbstractConnector implements IControleLaveLinge {

	@Override
	public void envoyerEtatAppareil(EtatAppareil etat) throws Exception {
		((LaveLinge) this.offering).setEtatAppareil(etat);
	}

	@Override
	public void envoyerPlanificationCycle(int heure, int minutes) throws Exception {
		((LaveLinge) this.offering).planifierCycle(heure, minutes);
	}

	@Override
	public void envoyerPlanificationMode(ModeLaveLinge ml, int heure, int minutes) throws Exception {
		((LaveLinge) this.offering).planifierMode(ml, heure, minutes);
	}

	@Override
	public void envoyerTemperature(TemperatureLaveLinge tl) throws Exception {
		((LaveLinge) this.offering).setTemperature(tl);
	}
}
