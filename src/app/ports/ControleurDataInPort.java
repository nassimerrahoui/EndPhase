package app.ports;

import app.interfaces.IControleur;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataOfferedI.DataI;
import fr.sorbonne_u.components.ports.AbstractDataInboundPort;

public class ControleurDataInPort extends AbstractDataInboundPort {
	
	private static final long serialVersionUID = 1L;

	public ControleurDataInPort(String uri, ComponentI owner) throws Exception {
		super(uri, DataOfferedI.PullI.class, DataOfferedI.PushI.class, owner);
	}
	
	@Override
	public DataI get() throws Exception {
		return ((IControleur) this.owner).getMessage(this.getClientPortURI());
	}
}
