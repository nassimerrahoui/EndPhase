package app.ports.lavelinge;

import java.util.ArrayList;

import app.components.LaveLinge;
import app.interfaces.appareil.ILaveLinge;
import app.util.ModeLaveLinge;
import app.util.TemperatureLaveLinge;
import app.util.URI;
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
	public void setModeLaveLinge(ModeLaveLinge etat) throws Exception {
		AbstractComponent.AbstractService<Void> task = new AbstractComponent.AbstractService<Void>() {
			public Void call() throws Exception {
				((LaveLinge) owner).setModeLaveLinge(etat);
				return null;
			}
		};

		this.owner.handleRequestAsync(URI.POOL_ACTION_LAVELINGE_URI.getURI(), task);

	}

	@Override
	public void planifierCycle(ArrayList<ModeLaveLinge> planification, int heure, int minutes) throws Exception {
		AbstractComponent.AbstractService<Void> task = new AbstractComponent.AbstractService<Void>() {
			public Void call() throws Exception {
				((LaveLinge) owner).planifierCycle(planification, heure, minutes);
				return null;
			}
		};

		this.owner.handleRequestAsync(URI.POOL_ACTION_LAVELINGE_URI.getURI(), task);

	}

	@Override
	public void setTemperature(TemperatureLaveLinge tl) throws Exception {
		AbstractComponent.AbstractService<Void> task = new AbstractComponent.AbstractService<Void>() {
			public Void call() throws Exception {
				((LaveLinge) owner).setTemperature(tl);
				return null;
			}
		};

		this.owner.handleRequestAsync(URI.POOL_ACTION_LAVELINGE_URI.getURI(), task);

	}

}
