package app.connectors;

import app.interfaces.appareil.IOrdinateur;
import app.interfaces.controleur.IControleOrdinateur;
import app.util.ModeOrdinateur;
import fr.sorbonne_u.components.connectors.AbstractConnector;

public class ControleurOrdinateurConnector extends AbstractConnector implements IControleOrdinateur {

	@Override
	public void envoyerModeOrdinateur(ModeOrdinateur etat) throws Exception {
		((IOrdinateur) this.offering).setModeOrdinateur(etat);
	}
}
