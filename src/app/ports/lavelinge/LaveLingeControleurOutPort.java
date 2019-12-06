package app.ports.lavelinge;

import app.interfaces.controleur.IControleur;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class LaveLingeControleurOutPort extends AbstractOutboundPort implements IControleur {

	private static final long serialVersionUID = 1L;

	public LaveLingeControleurOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, IControleur.class, owner);
	}
	
	public LaveLingeControleurOutPort(ComponentI owner) throws Exception {
		super(IControleur.class, owner);
	}

	@Override
	public void ajouterAppareil(String uri) throws Exception {
		((LaveLingeControleurOutPort) this.connector).ajouterAppareil(uri);
	}
}
