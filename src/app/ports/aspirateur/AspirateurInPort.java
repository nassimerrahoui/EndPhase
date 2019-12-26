package app.ports.aspirateur;

import app.components.Aspirateur;
import app.interfaces.appareil.IAspirateur;
import app.util.ModeAspirateur;
import app.util.URI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class AspirateurInPort extends AbstractInboundPort implements IAspirateur {

	private static final long serialVersionUID = 1L;

	public AspirateurInPort(String uri, ComponentI owner) throws Exception {
		super(uri, IAspirateur.class, owner);
	}
	
	public AspirateurInPort(ComponentI owner) throws Exception {
		super(IAspirateur.class, owner);
	}

	@Override
	public void setModeAspirateur(ModeAspirateur etat) throws Exception {
		AbstractComponent.AbstractService<Void> task = new AbstractComponent.AbstractService<Void>() {
			public Void call() throws Exception {
				((Aspirateur)owner).setModeAspirateur(etat);
				return null;
			}
		};

		this.owner.handleRequestAsync(URI.POOL_ACTION_ASPIRATEUR_URI.getURI(), task);
		
	}
}
