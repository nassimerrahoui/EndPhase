package app.ports.controleur;

import app.interfaces.controleur.IControleAspirateur;
import app.util.ModeAspirateur;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

/**
 * @author Willy Nassim
 */

public class ControleurAspirateurOutPort extends AbstractOutboundPort implements IControleAspirateur {

	private static final long serialVersionUID = 1L;

	public ControleurAspirateurOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, IControleAspirateur.class, owner);
	}
	
	public ControleurAspirateurOutPort(ComponentI owner) throws Exception {
		super(IControleAspirateur.class, owner);
	}

	@Override
	public void envoyerModeAspirateur(ModeAspirateur etat) throws Exception {
		((IControleAspirateur)this.connector).envoyerModeAspirateur(etat);
	}
}
