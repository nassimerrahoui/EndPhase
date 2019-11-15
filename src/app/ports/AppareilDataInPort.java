package app.ports;

import app.interfaces.IAppareil;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataOfferedI.DataI;
import fr.sorbonne_u.components.ports.AbstractDataInboundPort;

public class AppareilDataInPort extends AbstractDataInboundPort {
	
	private static final long serialVersionUID = 1L;

	public AppareilDataInPort(String uri, ComponentI owner) throws Exception {
		super(uri, DataOfferedI.PullI.class, DataOfferedI.PushI.class, owner);
	}
	
	@Override
	public DataI get() throws Exception {
		return ((IAppareil) this.owner).getConsommation();
	}
}
