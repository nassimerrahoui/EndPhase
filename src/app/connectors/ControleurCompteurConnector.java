package app.connectors;

import app.components.Compteur;
import app.interfaces.controleur.IControleCompteur;
import fr.sorbonne_u.components.connectors.AbstractConnector;

public class ControleurCompteurConnector extends AbstractConnector implements IControleCompteur {

	@Override
	public void demanderAjoutAppareil(String uri) throws Exception {
		((Compteur) this.offering).ajouterAppareil(uri);
	}

	@Override
	public void demanderAjoutUniteProduction(String uri) throws Exception {
		((Compteur) this.offering).ajouterUniteProduction(uri);
	}

	@Override
	public double getConsommationGlobale() throws Exception {
		return ((Compteur) this.offering).envoyerConsommationGlobale();
	}

	@Override
	public double getProductionGlobale() throws Exception {
		return ((Compteur) this.offering).envoyerProductionGlobale();
	}
}
