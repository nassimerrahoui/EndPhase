package app.ports.frigo;

import app.interfaces.appareil.IConsommation;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class FrigoCompteurOutPort extends AbstractOutboundPort implements IConsommation {

	private static final long serialVersionUID = 1L;

	public FrigoCompteurOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, IConsommation.class, owner);
	}
	
	public FrigoCompteurOutPort(ComponentI owner) throws Exception {
		super(IConsommation.class, owner);
	}

	@Override
	public double envoyerConsommation(String uri) throws Exception {
		return ((IConsommation) this.connector).envoyerConsommation(uri);
	}
}
