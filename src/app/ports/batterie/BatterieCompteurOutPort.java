package app.ports.batterie;

import app.interfaces.production.IProduction;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class BatterieCompteurOutPort extends AbstractOutboundPort implements IProduction {
	private static final long serialVersionUID = 1L;

	public BatterieCompteurOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, IProduction.class, owner);
	}
	
	public BatterieCompteurOutPort(ComponentI owner) throws Exception {
		super(IProduction.class, owner);
	}

	@Override
	public void envoyerProduction(String uri, double production) throws Exception {
		((IProduction) this.connector).envoyerProduction(uri, production);
	}
}
