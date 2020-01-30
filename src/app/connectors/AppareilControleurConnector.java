package app.connectors;

import app.interfaces.appareil.IAjoutAppareil;
import app.interfaces.controleur.IControleur;
import app.util.TypeAppareil;
import fr.sorbonne_u.components.connectors.AbstractConnector;

public class AppareilControleurConnector extends AbstractConnector implements IAjoutAppareil {

	@Override
	public void demandeAjoutControleur(String uri, String classe, TypeAppareil type) throws Exception {
		((IControleur) this.offering).ajouterAppareil(uri, classe, type);
	}
}
