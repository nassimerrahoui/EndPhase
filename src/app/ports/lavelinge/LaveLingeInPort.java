package app.ports.lavelinge;

import app.components.LaveLinge;
import app.interfaces.appareil.ILaveLinge;
import app.util.EtatAppareil;
import app.util.ModeLaveLinge;
import app.util.TemperatureLaveLinge;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class LaveLingeInPort extends AbstractInboundPort implements ILaveLinge {

	private static final long serialVersionUID = 1L;

	public LaveLingeInPort(String uri, ComponentI owner) throws Exception {
		super(uri, ILaveLinge.class, owner);
	}

	public LaveLingeInPort(ComponentI owner) throws Exception {
		super(ILaveLinge.class, owner);
	}

	@Override
	public void setEtatAppareil(EtatAppareil etat) throws Exception {
		AbstractComponent.AbstractService<Void> task = new AbstractComponent.AbstractService<Void>() {
			public Void call() throws Exception {
				((LaveLinge) owner).setEtatAppareil(etat);
				return null;
			}
		};

		this.owner.handleRequestAsync(0, task);

	}

	@Override
	public void planifierCycle(int heure, int minutes) throws Exception {
		AbstractComponent.AbstractService<Void> task = new AbstractComponent.AbstractService<Void>() {
			public Void call() throws Exception {
				((LaveLinge) owner).planifierCycle(heure, minutes);
				return null;
			}
		};

		this.owner.handleRequestAsync(0, task);

	}

	@Override
	public void planifierMode(ModeLaveLinge ml, int heure, int minutes) throws Exception {
		AbstractComponent.AbstractService<Void> task = new AbstractComponent.AbstractService<Void>() {
			public Void call() throws Exception {
				((LaveLinge) owner).planifierMode(ml, heure, minutes);
				return null;
			}
		};

		this.owner.handleRequestAsync(0, task);

	}

	@Override
	public void setTemperature(TemperatureLaveLinge tl) throws Exception {
		AbstractComponent.AbstractService<Void> task = new AbstractComponent.AbstractService<Void>() {
			public Void call() throws Exception {
				((LaveLinge) owner).setTemperature(tl);
				return null;
			}
		};

		this.owner.handleRequestAsync(0, task);

	}

}
