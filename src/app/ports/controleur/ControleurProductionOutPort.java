package app.ports.controleur;

import app.interfaces.production.IProduction;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class ControleurProductionOutPort extends AbstractOutboundPort implements IProduction {

	private static final long serialVersionUID = 1L;

	public ControleurProductionOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, IProduction.class, owner);
	}
	
	public ControleurProductionOutPort(ComponentI owner) throws Exception {
		super(IProduction.class, owner);
	}

	@Override
	public double getProduction() throws Exception {
		return ((ControleurProductionOutPort) this.connector).getProduction();
	}
}