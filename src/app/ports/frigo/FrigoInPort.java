package app.ports.frigo;

import app.components.Frigo;
import app.interfaces.appareil.IFrigo;
import app.util.EtatAppareil;
import app.util.ModeFrigo;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class FrigoInPort extends AbstractInboundPort implements IFrigo {

	private static final long serialVersionUID = 1L;

	public FrigoInPort(String uri, ComponentI owner) throws Exception {
		super(uri, IFrigo.class, owner);
	}

	@Override
	public void setEtatAppareil(EtatAppareil etat) throws Exception {

		AbstractComponent.AbstractService<Void> task = new AbstractComponent.AbstractService<Void>() {
			public Void call() throws Exception {
				((Frigo) owner).setEtatAppareil(etat);
				return null;
			}
		};

		this.owner.handleRequestAsync(0, task);

	}

	@Override
	public void setTemperature_Refrigerateur(double temperature) throws Exception {
		AbstractComponent.AbstractService<Void> task = new AbstractComponent.AbstractService<Void>() {
			public Void call() throws Exception {
				((Frigo) owner).setTemperature_Refrigerateur(temperature);
				return null;
			}
		};

		this.owner.handleRequestAsync(0, task);
	}

	@Override
	public void setTemperature_Congelateur(double temperature) throws Exception {
		AbstractComponent.AbstractService<Void> task = new AbstractComponent.AbstractService<Void>() {
			public Void call() throws Exception {
				((Frigo) owner).setTemperature_Congelateur(temperature);
				return null;
			}
		};

		this.owner.handleRequestAsync(0, task);
	}

	@Override
	public void setLumiere_Refrigerateur(ModeFrigo mf) throws Exception {
		AbstractComponent.AbstractService<Void> task = new AbstractComponent.AbstractService<Void>() {
			public Void call() throws Exception {
				((Frigo) owner).setLumiere_Refrigerateur(mf);
				return null;
			}
		};

		this.owner.handleRequestAsync(0, task);
	}

	@Override
	public void setLumiere_Congelateur(ModeFrigo mf) throws Exception {
		AbstractComponent.AbstractService<Void> task = new AbstractComponent.AbstractService<Void>() {
			public Void call() throws Exception {
				((Frigo) owner).setLumiere_Congelateur(mf);
				return null;
			}
		};

		this.owner.handleRequestAsync(0, task);
	}

}
