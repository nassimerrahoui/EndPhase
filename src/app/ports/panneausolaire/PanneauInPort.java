package app.ports.panneausolaire;

import app.components.PanneauSolaire;
import app.interfaces.production.IPanneau;
import app.util.EtatUniteProduction;
import app.util.URI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class PanneauInPort extends AbstractInboundPort implements IPanneau {

	private static final long serialVersionUID = 1L;

	public PanneauInPort(ComponentI owner) throws Exception {
		super(IPanneau.class, owner);
	}

	public PanneauInPort(String uri, ComponentI owner) throws Exception {
		super(uri, IPanneau.class, owner);
	}
	
	@Override
	public void setEtatUniteProduction(EtatUniteProduction etat) throws Exception {
		AbstractComponent.AbstractService<Void> task = new AbstractComponent.AbstractService<Void>() {
			public Void call() throws Exception {
				((PanneauSolaire) owner).setEtatUniteProduction(etat);
				return null;
			}
		};

		this.owner.handleRequestAsync(URI.POOL_ACTION_PANNEAUSOLAIRE_URI.getURI(), task);
	}

}