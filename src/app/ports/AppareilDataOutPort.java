package app.ports;

import app.interfaces.IAppareil;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataRequiredI.DataI;
import fr.sorbonne_u.components.ports.AbstractDataOutboundPort;

public class AppareilDataOutPort extends AbstractDataOutboundPort {

	private static final long serialVersionUID = 1L;

	public AppareilDataOutPort(String uri, Class<?> implementedInterface, ComponentI owner) throws Exception {
		super(uri, implementedInterface, owner);
	}

	@Override
	public void receive(DataI d) throws Exception {
		this.owner.handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((IAppareil) this.getServiceOwner()).recevoirMessage((DataOfferedI.DataI) d);
				return null;
			}
		});
	}
}
