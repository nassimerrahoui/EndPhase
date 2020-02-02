package app.connectors;

import app.interfaces.compteur.ICompteur;
import app.interfaces.production.IProduction;
import fr.sorbonne_u.components.connectors.AbstractConnector;

/**
 * @author Willy Nassim
 */

public class UniteCompteurConnector extends AbstractConnector implements IProduction {

	@Override
	public void envoyerProduction(String uri, double production) throws Exception {
		((ICompteur) this.offering).setUniteProduction(uri, production);
	}
}
