package app.connectors;

import app.components.Controleur;
import app.interfaces.production.IAjoutUniteProduction;
import fr.sorbonne_u.components.connectors.AbstractConnector;

public class UniteControleurConnector extends AbstractConnector implements IAjoutUniteProduction {

	@Override
	public void demandeAjoutControleur(String uri) throws Exception {
		((Controleur) this.offering).ajouterUniteProduction(uri);
	}
}
