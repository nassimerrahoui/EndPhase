package app.ports.ordi;

import app.interfaces.controleur.IControleur;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class OrdinateurControleurOutPort extends AbstractOutboundPort implements IControleur {

	private static final long serialVersionUID = 1L;

	public OrdinateurControleurOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, IControleur.class, owner);
	}
	
	public OrdinateurControleurOutPort(ComponentI owner) throws Exception {
		super(IControleur.class, owner);
	}

	@Override
	public void ajouterAppareil(String uri) throws Exception {
		((OrdinateurControleurOutPort) this.connector).ajouterAppareil(uri);
	}
}
