package app.ports.frigo;

import app.components.Frigo;
import app.interfaces.appareil.IConsommation;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class FrigoCompteurInPort extends AbstractInboundPort implements IConsommation {

	private static final long serialVersionUID = 1L;

	public FrigoCompteurInPort(String uri, ComponentI owner) throws Exception {
		super(uri, IConsommation.class, owner);
	}
	
	public FrigoCompteurInPort(ComponentI owner) throws Exception {
		super(IConsommation.class, owner);
	}

	@Override
	public double envoyerConsommation() throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((Frigo) owner).envoyerConsommation());
	}

}
