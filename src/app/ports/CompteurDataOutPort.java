package app.ports;

import app.data.Message;
import app.interfaces.ICompteur;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataRequiredI;
import fr.sorbonne_u.components.ports.AbstractDataOutboundPort;

public class CompteurDataOutPort extends AbstractDataOutboundPort {

	private static final long serialVersionUID = 1L;

	public CompteurDataOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, DataOfferedI.PullI.class, DataOfferedI.PushI.class, owner);
	}

	@Override
	public void receive(DataRequiredI.DataI d) throws Exception {
		this.owner.handleRequestAsync(new AbstractComponent.AbstractService<Void>() {

			public Void call() throws Exception {
				((ICompteur) this.getServiceOwner()).recevoirMessage((Message) d);
				return null;
			}
		});
	}
}
