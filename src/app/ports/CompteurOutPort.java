package app.ports;

import app.interfaces.ICompteur;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class CompteurOutPort extends AbstractOutboundPort implements ICompteur {

	private static final long serialVersionUID = 1L;

	public CompteurOutPort(ComponentI owner) throws Exception {
		super(ICompteur.class, owner);
	}
	
	public CompteurOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, ICompteur.class, owner);
	}

	@Override
	public double getAllConsommations() throws Exception {
		return ((ICompteur) this.connector).getAllConsommations();
	}

	@Override
	public void ajouterAppareil(String uri) throws Exception {
		((ICompteur) this.connector).ajouterAppareil(uri);
	}
}
