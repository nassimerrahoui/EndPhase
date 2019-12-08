package app.connectors;

import app.components.Batterie;
import app.interfaces.controleur.IControleBatterie;
import app.util.EtatUniteProduction;
import fr.sorbonne_u.components.connectors.AbstractConnector;

public class ControleurBatterieConnector extends AbstractConnector implements IControleBatterie {

	@Override
	public void envoyerEtatUniteProduction(EtatUniteProduction etat) throws Exception {
		((Batterie) this.offering).setEtatUniteProduction(etat);
	}
}
