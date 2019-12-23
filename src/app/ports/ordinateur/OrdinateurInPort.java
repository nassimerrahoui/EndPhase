package app.ports.ordinateur;

import app.components.Ordinateur;
import app.interfaces.appareil.IOrdinateur;
import app.util.ModeOrdinateur;
import app.util.URI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class OrdinateurInPort extends AbstractInboundPort implements IOrdinateur {

	private static final long serialVersionUID = 1L;

	public OrdinateurInPort(String uri, ComponentI owner) throws Exception {
		super(uri, IOrdinateur.class, owner);
	}
	
	public OrdinateurInPort(ComponentI owner) throws Exception {
		super(IOrdinateur.class, owner);
	}

	@Override
	public void setModeOrdinateur(ModeOrdinateur etat) throws Exception {
		AbstractComponent.AbstractService<Void> task = new AbstractComponent.AbstractService<Void>() {
			public Void call() throws Exception {
				((Ordinateur)owner).setModeOrdinateur(etat);
				return null;
			}
		};

		this.owner.handleRequestAsync(URI.POOL_ACTION_ORDINATEUR_URI.getURI(), task);
		
	}
}
