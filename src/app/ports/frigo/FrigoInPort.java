package app.ports.frigo;

import app.components.Frigo;
import app.interfaces.appareil.IFrigo;
import app.util.ModeFrigo;
import app.util.URI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;


/**
 * @author Willy Nassim
 */

public class FrigoInPort extends AbstractInboundPort implements IFrigo {

	private static final long serialVersionUID = 1L;

	public FrigoInPort(String uri, ComponentI owner) throws Exception {
		super(uri, IFrigo.class, owner);
	}
	
	public FrigoInPort(ComponentI owner) throws Exception {
		super(IFrigo.class, owner);
	}

	@Override
	public void setModeFrigo(ModeFrigo etat) throws Exception {

		AbstractComponent.AbstractService<Void> task = new AbstractComponent.AbstractService<Void>() {
			public Void call() throws Exception {
				((Frigo) owner).setModeFrigo(etat);
				return null;
			}
		};

		this.owner.handleRequestAsync(URI.POOL_ACTION_FRIGO_URI.getURI(), task);

	}

	@Override
	public void setTemperature_Refrigerateur(double temperature) throws Exception {
		AbstractComponent.AbstractService<Void> task = new AbstractComponent.AbstractService<Void>() {
			public Void call() throws Exception {
				((Frigo) owner).setTemperature_Refrigerateur(temperature);
				return null;
			}
		};

		this.owner.handleRequestAsync(URI.POOL_ACTION_FRIGO_URI.getURI(), task);
	}

	@Override
	public void setTemperature_Congelateur(double temperature) throws Exception {
		AbstractComponent.AbstractService<Void> task = new AbstractComponent.AbstractService<Void>() {
			public Void call() throws Exception {
				((Frigo) owner).setTemperature_Congelateur(temperature);
				return null;
			}
		};

		this.owner.handleRequestAsync(URI.POOL_ACTION_FRIGO_URI.getURI(), task);
	}
}
