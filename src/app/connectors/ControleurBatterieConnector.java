package app.connectors;

import app.interfaces.controleur.IControleBatterie;
import app.interfaces.production.IBatterie;
import app.util.EtatUniteProduction;
import fr.sorbonne_u.components.connectors.AbstractConnector;

public class ControleurBatterieConnector extends AbstractConnector implements IControleBatterie {

	@Override
	public void envoyerEtatUniteProduction(EtatUniteProduction etat) throws Exception {
		((IBatterie) this.offering).setEtatUniteProduction(etat);
	}
}
