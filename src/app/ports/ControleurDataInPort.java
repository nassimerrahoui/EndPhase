package app.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.DataOfferedI.DataI;
import fr.sorbonne_u.components.ports.AbstractDataInboundPort;

public class ControleurDataInPort extends AbstractDataInboundPort {

	private static final long serialVersionUID = 1L;

	public ControleurDataInPort(String uri, Class<?> implementedInterface, ComponentI owner) throws Exception {
		super(uri, implementedInterface, owner);
	}

	@Override
	public DataI get() throws Exception {
		return null;
	}
}
