package app.connectors;

import app.interfaces.appareil.IAjoutAppareil;
import app.interfaces.controleur.IControleur;
import fr.sorbonne_u.components.connectors.AbstractConnector;

public class AppareilControleurConnector extends AbstractConnector implements IAjoutAppareil {

	@Override
	public void demandeAjoutControleur(String uri) throws Exception {
		((IControleur) this.offering).ajouterAppareil(uri);
	}
}
