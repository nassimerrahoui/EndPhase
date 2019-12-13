package app.ports.controleur;

import app.components.Controleur;
import app.interfaces.controleur.IControleur;
import app.util.URI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class ControleurInPort extends AbstractInboundPort implements IControleur {

	private static final long serialVersionUID = 1L;

	public ControleurInPort(String uri, ComponentI owner) throws Exception {
		super(uri, IControleur.class, owner);
	}
	
	public ControleurInPort(ComponentI owner) throws Exception {
		super(IControleur.class, owner);
	}

	@Override
	public void ajouterAppareil(String uri) throws Exception {
		AbstractComponent.AbstractService<Void> task = new AbstractComponent.AbstractService<Void>() {
			public Void call() throws Exception {
				((Controleur) owner).ajouterAppareil(uri);
				return null;
			}
		};

		this.owner.handleRequestAsync(URI.POOL_AJOUT_CONTROLEUR_URI.getURI(), task);
	}

	@Override
	public void ajouterUniteProduction(String uri) throws Exception {
		AbstractComponent.AbstractService<Void> task = new AbstractComponent.AbstractService<Void>() {
			public Void call() throws Exception {
				((Controleur) owner).ajouterUniteProduction(uri);
				return null;
			}
		};

		this.owner.handleRequestAsync(URI.POOL_AJOUT_CONTROLEUR_URI.getURI(), task);
	}
}
