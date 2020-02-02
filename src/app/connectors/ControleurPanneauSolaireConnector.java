package app.connectors;

import app.interfaces.controleur.IControlePanneau;
import app.interfaces.production.IPanneau;
import app.util.EtatUniteProduction;
import fr.sorbonne_u.components.connectors.AbstractConnector;

/**
 * @author Willy Nassim
 */

public class ControleurPanneauSolaireConnector extends AbstractConnector implements IControlePanneau {

	@Override
	public void envoyerEtatUniteProduction(EtatUniteProduction etat) throws Exception {
		((IPanneau) this.offering).setEtatUniteProduction(etat);
	}
}
