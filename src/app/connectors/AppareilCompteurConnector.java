package app.connectors;

import app.components.Compteur;
import app.interfaces.appareil.IConsommation;
import fr.sorbonne_u.components.connectors.AbstractConnector;

public class AppareilCompteurConnector extends AbstractConnector implements IConsommation {

	@Override
	public void envoyerConsommation(String uri, double consommation) throws Exception {
		((Compteur) this.offering).setAppareilConsommation(uri, consommation);
	}
}
