package app.ports.batterie;

import app.components.Batterie;
import app.interfaces.production.IProduction;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class BatterieCompteurInPort extends AbstractInboundPort implements IProduction{
	private static final long serialVersionUID = 1L;

	public BatterieCompteurInPort(String uri, ComponentI owner) throws Exception {
		super(uri, IProduction.class, owner);
	}
	
	public BatterieCompteurInPort(ComponentI owner) throws Exception {
		super(IProduction.class, owner);
	}


	@Override
	public double getProduction() throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((Batterie) owner).getProduction());
	}
}
