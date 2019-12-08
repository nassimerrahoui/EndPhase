package app.ports.ordi;

import app.components.Ordinateur;
import app.interfaces.appareil.IOrdinateur;
import app.util.EtatAppareil;
import app.util.ModeOrdinateur;
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
	public void setEtatAppareil(EtatAppareil etat) throws Exception {
		AbstractComponent.AbstractService<Void> task = new AbstractComponent.AbstractService<Void>() {
			public Void call() throws Exception {
				((Ordinateur)owner).setEtatAppareil(etat);
				return null;
			}
		};

		this.owner.handleRequestAsync(0, task);
		
	}

	@Override
	public void setMode(ModeOrdinateur mo) throws Exception {
		AbstractComponent.AbstractService<Void> task = new AbstractComponent.AbstractService<Void>() {
			public Void call() throws Exception {
				((Ordinateur)owner).setMode(mo);
				return null;
			}
		};

		this.owner.handleRequestAsync(0, task);
		
	}

}
