package app.ports.frigo;

import app.interfaces.controleur.IControleur;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class FrigoControleurOutPort extends AbstractOutboundPort implements IControleur {

	private static final long serialVersionUID = 1L;

	public FrigoControleurOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, IControleur.class, owner);
	}
	
	public FrigoControleurOutPort(ComponentI owner) throws Exception {
		super(IControleur.class, owner);
	}

	@Override
	public void ajouterAppareil(String uri) throws Exception {
		((FrigoControleurOutPort) this.connector).ajouterAppareil(uri);
	}
}
