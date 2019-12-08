package app.connectors;

import app.components.Ordinateur;
import app.interfaces.controleur.IControleOrdinateur;
import app.util.EtatAppareil;
import app.util.ModeOrdinateur;
import fr.sorbonne_u.components.connectors.AbstractConnector;

public class ControleurOrdinateurConnector extends AbstractConnector implements IControleOrdinateur {

	@Override
	public void envoyerEtatAppareil(EtatAppareil etat) throws Exception {
		((Ordinateur) this.offering).setEtatAppareil(etat);
	}

	@Override
	public void envoyerMode(ModeOrdinateur mo) throws Exception {
		((Ordinateur) this.offering).setMode(mo);
	}
}
