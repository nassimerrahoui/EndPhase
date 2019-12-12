package app.connectors;

import app.interfaces.appareil.IConsommation;
import app.interfaces.compteur.ICompteur;
import fr.sorbonne_u.components.connectors.AbstractConnector;

public class AppareilCompteurConnector extends AbstractConnector implements IConsommation {

	@Override
	public void envoyerConsommation(String uri, double consommation) throws Exception {
		((ICompteur) this.offering).setAppareilConsommation(uri, consommation);
	}
}
