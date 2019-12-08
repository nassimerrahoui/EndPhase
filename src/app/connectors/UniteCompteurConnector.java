package app.connectors;

import app.components.Compteur;
import app.interfaces.production.IProduction;
import fr.sorbonne_u.components.connectors.AbstractConnector;

public class UniteCompteurConnector extends AbstractConnector implements IProduction {

	@Override
	public void envoyerProduction(String uri, double production) throws Exception {
		((Compteur) this.offering).setUniteProduction(uri, production);
	}
}
