package app.connectors;

import app.components.Controleur;
import app.interfaces.appareil.IAjoutAppareil;
import fr.sorbonne_u.components.connectors.AbstractConnector;

public class AppareilControleurConnector extends AbstractConnector implements IAjoutAppareil {

	@Override
	public void demandeAjoutControleur(String uri) throws Exception {
		((Controleur) this.offering).ajouterAppareil(uri);
	}
}
