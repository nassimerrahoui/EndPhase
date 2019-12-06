package app.ports.ordi;

import app.components.Ordinateur;
import app.interfaces.IConsommation;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class OrdinateurConsoInPort extends AbstractInboundPort implements IConsommation{
	private static final long serialVersionUID = 1L;

	public OrdinateurConsoInPort(String uri, ComponentI owner) throws Exception {
		super(uri, IConsommation.class, owner);
	}

	@Override
	public double getConsommation() throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((Ordinateur) owner).getConsommation());
	}
}
