package app.connectors;

import app.interfaces.compteur.ICompteurControleur;
import app.interfaces.controleur.IControleCompteur;
import fr.sorbonne_u.components.connectors.AbstractConnector;

public class ControleurCompteurConnector extends AbstractConnector implements IControleCompteur {

	@Override
	public void demanderAjoutAppareil(String uri) throws Exception {
		((ICompteurControleur) this.offering).ajouterAppareil(uri);
	}

	@Override
	public void demanderAjoutUniteProduction(String uri) throws Exception {
		((ICompteurControleur) this.offering).ajouterUniteProduction(uri);
	}

	@Override
	public double getConsommationGlobale() throws Exception {
		return ((ICompteurControleur) this.offering).envoyerConsommationGlobale();
	}

	@Override
	public double getProductionGlobale() throws Exception {
		return ((ICompteurControleur) this.offering).envoyerProductionGlobale();
	}
}
