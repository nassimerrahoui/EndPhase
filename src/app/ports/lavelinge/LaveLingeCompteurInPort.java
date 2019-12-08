package app.ports.lavelinge;

import app.components.LaveLinge;
import app.interfaces.appareil.IConsommation;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class LaveLingeCompteurInPort extends AbstractInboundPort implements IConsommation {

	private static final long serialVersionUID = 1L;

	public LaveLingeCompteurInPort(String uri, ComponentI owner) throws Exception {
		super(uri, IConsommation.class, owner);
	}

	public LaveLingeCompteurInPort(ComponentI owner) throws Exception {
		super(IConsommation.class, owner);
	}

	@Override
	public double envoyerConsommation() throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((LaveLinge) owner).envoyerConsommation());
	}
}
