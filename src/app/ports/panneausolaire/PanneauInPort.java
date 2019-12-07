package app.ports.panneausolaire;

import app.components.PanneauSolaire;
import app.interfaces.production.IBatterie;
import app.util.EtatUniteProduction;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class PanneauInPort extends AbstractInboundPort implements IBatterie {

	private static final long serialVersionUID = 1L;

	public PanneauInPort(ComponentI owner) throws Exception {
		super(IBatterie.class, owner);
	}

	public PanneauInPort(String uri, ComponentI owner) throws Exception {
		super(uri, IBatterie.class, owner);
	}
	
	@Override
	public void setEtatUniteProduction(EtatUniteProduction etat) throws Exception {
		AbstractComponent.AbstractService<Void> task = new AbstractComponent.AbstractService<Void>() {
			public Void call() throws Exception {
				((PanneauSolaire) owner).setEtatUniteProduction(etat);
				return null;
			}
		};

		this.owner.handleRequestAsync(0, task);
	}

}