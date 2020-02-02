package app.ports.batterie;

import app.components.Batterie;
import app.interfaces.production.IBatterie;
import app.util.EtatUniteProduction;
import app.util.URI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

/**
 * @author Willy Nassim
 */

public class BatterieInPort extends AbstractInboundPort implements IBatterie {

	private static final long serialVersionUID = 1L;

	public BatterieInPort(ComponentI owner) throws Exception {
		super(IBatterie.class, owner);
	}

	public BatterieInPort(String uri, ComponentI owner) throws Exception {
		super(uri, IBatterie.class, owner);
	}
	
	@Override
	public void setEtatUniteProduction(EtatUniteProduction etat) throws Exception {
		AbstractComponent.AbstractService<Void> task = new AbstractComponent.AbstractService<Void>() {
			public Void call() throws Exception {
				((Batterie) owner).setEtatUniteProduction(etat);
				return null;
			}
		};

		this.owner.handleRequestAsync(URI.POOL_ACTION_BATTERIE_URI.getURI(), task);
	}

}
