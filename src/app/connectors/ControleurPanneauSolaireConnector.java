package app.connectors;

import app.components.PanneauSolaire;
import app.interfaces.controleur.IControlePanneau;
import app.util.EtatUniteProduction;
import fr.sorbonne_u.components.connectors.AbstractConnector;

public class ControleurPanneauSolaireConnector extends AbstractConnector implements IControlePanneau {

	@Override
	public void envoyerEtatUniteProduction(EtatUniteProduction etat) throws Exception {
		((PanneauSolaire) this.offering).setEtatUniteProduction(etat);
	}
}
