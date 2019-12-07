package app.ports.ordi;

import app.components.Ordinateur;
import app.interfaces.appareil.IConsommation;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class OrdinateurConsoInPort extends AbstractInboundPort implements IConsommation {
	private static final long serialVersionUID = 1L;

	public OrdinateurConsoInPort(String uri, ComponentI owner) throws Exception {
		super(uri, IConsommation.class, owner);
	}

	public OrdinateurConsoInPort(ComponentI owner) throws Exception {
		super(IConsommation.class, owner);
	}

	@Override
	public double envoyerConsommation() throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((Ordinateur) owner).envoyerConsommation());
	}
}
