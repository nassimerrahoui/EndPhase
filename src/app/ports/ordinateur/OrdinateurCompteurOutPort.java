package app.ports.ordinateur;

import app.interfaces.appareil.IConsommation;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class OrdinateurCompteurOutPort extends AbstractOutboundPort implements IConsommation {

	private static final long serialVersionUID = 1L;

	public OrdinateurCompteurOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, IConsommation.class, owner);
	}
	
	public OrdinateurCompteurOutPort(ComponentI owner) throws Exception {
		super(IConsommation.class, owner);
	}

	@Override
	public void envoyerConsommation(String uri, double consommation) throws Exception {
		((IConsommation) this.connector).envoyerConsommation(uri, consommation);
	}
}
