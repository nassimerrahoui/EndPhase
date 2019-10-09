package app.ports;

import app.components.Frigo;
import app.interfaces.IFrigo;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class FrigoInPort extends AbstractInboundPort implements IFrigo {

	private static final long serialVersionUID = 1L;

	public FrigoInPort(String uri, Class<?> implementedInterface, ComponentI owner) throws Exception {
		super(uri, implementedInterface, owner);
	}

	@Override
	public void setOn() throws Exception {
		AbstractComponent.AbstractService<Void> task = new AbstractComponent.AbstractService<Void>() {

			public Void call() throws Exception {
				((Frigo) this.serviceOwner).setOn();
				return null;
			}
		};

		this.owner.handleRequestAsync(0, task);
	}

	@Override
	public void setOff() throws Exception {
		AbstractComponent.AbstractService<Void> task = new AbstractComponent.AbstractService<Void>() {

			public Void call() throws Exception {
				((Frigo) this.serviceOwner).setOff();
				return null;
			}
		};
		
		this.owner.handleRequestAsync(0, task);
	}

	@Override
	public void setFreezerTemperatureCible(Double t) throws Exception {
		AbstractComponent.AbstractService<Void> task = new AbstractComponent.AbstractService<Void>() {

			public Void call() throws Exception {
				((Frigo) this.serviceOwner).setFreezerTemperatureCible(t);
				return null;
			}
		};
		
		this.owner.handleRequestAsync(0, task);
		
	}

	@Override
	public void setFridgeTemperatureCible(Double t) throws Exception {
		AbstractComponent.AbstractService<Void> task = new AbstractComponent.AbstractService<Void>() {

			public Void call() throws Exception {
				((Frigo) this.serviceOwner).setFridgeTemperatureCible(t);
				return null;
			}
		};
		
		this.owner.handleRequestAsync(0, task);
		
	}

}
