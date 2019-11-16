package app.ports;

import app.data.Message;
import app.interfaces.IControleur;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataRequiredI;
import fr.sorbonne_u.components.ports.AbstractDataOutboundPort;

public class ControleurDataOutPort extends AbstractDataOutboundPort {
	private static final long serialVersionUID = 1L;

	public ControleurDataOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, DataOfferedI.PullI.class, DataOfferedI.PushI.class, owner);
	}

	@Override
	public void receive(DataRequiredI.DataI d) throws Exception {
		
		AbstractComponent.AbstractService<Void> task = new AbstractComponent.AbstractService<Void>() {

			public Void call() throws Exception {
				((IControleur) this.getServiceOwner()).getEnergie((Message) d);
				return null;
			}
		};

		this.owner.handleRequestAsync(0, task);
	}
}
