package app.ports;

import app.data.Message;

import app.interfaces.IUProduction;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataRequiredI;
import fr.sorbonne_u.components.ports.AbstractDataOutboundPort;

public class UProductionDataOutPort extends AbstractDataOutboundPort {

	private static final long serialVersionUID = 1L;

	public UProductionDataOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, DataOfferedI.PullI.class, DataOfferedI.PushI.class, owner);
	}

	@Override
	public void receive(DataRequiredI.DataI d) throws Exception {
		this.owner.handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((IUProduction) this.getServiceOwner()).recevoirMessage((Message) d);
				return null;
			}
		});
	}
}
