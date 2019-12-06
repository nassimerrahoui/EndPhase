package app.ports.compteur;

import app.components.Frigo;
import app.interfaces.appareil.IConsommation;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class CompteurInPort extends AbstractInboundPort implements IConsommation {

	private static final long serialVersionUID = 1L;

	public CompteurInPort(String uri, ComponentI owner) throws Exception {
		super(uri, IConsommation.class, owner);
	}

	@Override
	public double getConsommation() throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((Frigo) owner).getConsommation());
	}

}
