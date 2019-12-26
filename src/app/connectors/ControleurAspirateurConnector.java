package app.connectors;

import app.interfaces.appareil.IAspirateur;
import app.interfaces.controleur.IControleAspirateur;
import app.util.ModeAspirateur;
import fr.sorbonne_u.components.connectors.AbstractConnector;

public class ControleurAspirateurConnector extends AbstractConnector implements IControleAspirateur {

	@Override
	public void envoyerModeAspirateur(ModeAspirateur etat) throws Exception {
		((IAspirateur) this.offering).setModeAspirateur(etat);
	}
}
