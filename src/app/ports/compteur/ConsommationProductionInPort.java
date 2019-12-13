package app.ports.compteur;

import app.components.Compteur;
import app.interfaces.compteur.ICompteur;
import app.util.URI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class ConsommationProductionInPort extends AbstractInboundPort implements ICompteur {

	private static final long serialVersionUID = 1L;

	public ConsommationProductionInPort(String uri, ComponentI owner) throws Exception {
		super(uri, ICompteur.class, owner);
	}
	
	public ConsommationProductionInPort(ComponentI owner) throws Exception {
		super(ICompteur.class, owner);
	}

	@Override
	public void setAppareilConsommation(String uri, double consommation) throws Exception {
		AbstractComponent.AbstractService<Void> task = new AbstractComponent.AbstractService<Void>() {
			public Void call() throws Exception {
				((Compteur) owner).setAppareilConsommation(uri, consommation);
				return null;
			}
		};

		this.owner.handleRequestAsync(URI.POOL_CONSO_PROD_COMPTEUR_URI.getURI(), task);
	}

	@Override
	public void setUniteProduction(String uri, double production) throws Exception {
		AbstractComponent.AbstractService<Void> task = new AbstractComponent.AbstractService<Void>() {
			public Void call() throws Exception {
				((Compteur) owner).setUniteProduction(uri, production);
				return null;
			}
		};

		this.owner.handleRequestAsync(URI.POOL_CONSO_PROD_COMPTEUR_URI.getURI(), task);
	}

}
