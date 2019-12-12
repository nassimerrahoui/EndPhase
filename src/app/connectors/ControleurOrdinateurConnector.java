package app.connectors;

import app.interfaces.appareil.IOrdinateur;
import app.interfaces.controleur.IControleOrdinateur;
import app.util.EtatAppareil;
import app.util.ModeOrdinateur;
import fr.sorbonne_u.components.connectors.AbstractConnector;

public class ControleurOrdinateurConnector extends AbstractConnector implements IControleOrdinateur {

	@Override
	public void envoyerEtatAppareil(EtatAppareil etat) throws Exception {
		((IOrdinateur) this.offering).setEtatAppareil(etat);
	}

	@Override
	public void envoyerMode(ModeOrdinateur mo) throws Exception {
		((IOrdinateur) this.offering).setMode(mo);
	}
}
