package app.ports.controleur;

import app.interfaces.IControleur;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class ControleurFrigoOutPort extends AbstractOutboundPort implements IControleur{

	private static final long serialVersionUID = 1L;

	public ControleurFrigoOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, IControleur.class, owner);
	}

}
