package app.ports.lavelinge;

import app.components.LaveLinge;
import app.interfaces.appareil.IConsommation;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class LaveLingeConsoInPort extends AbstractInboundPort implements IConsommation {

	private static final long serialVersionUID = 1L;

	public LaveLingeConsoInPort(String uri, ComponentI owner) throws Exception {
		super(uri, IConsommation.class, owner);
	}

	@Override
	public double getConsommation() throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((LaveLinge) owner).getConsommation());
	}

}
