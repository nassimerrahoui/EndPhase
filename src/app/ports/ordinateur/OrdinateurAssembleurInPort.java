package app.ports.ordinateur;

import app.components.Ordinateur;
import app.interfaces.generateur.IEntiteDynamique;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class OrdinateurAssembleurInPort extends AbstractInboundPort implements IEntiteDynamique {

	private static final long serialVersionUID = 1L;

	public OrdinateurAssembleurInPort(ComponentI owner) throws Exception {
		super(IEntiteDynamique.class, owner);
	}

	public OrdinateurAssembleurInPort(String uri, ComponentI owner) throws Exception {
		super(IEntiteDynamique.class, owner);
	}

	@Override
	public void demanderAjoutLogement(String uri) throws Exception {
		this.owner.handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((Ordinateur) this.getServiceOwner()).demandeAjoutControleur(uri);
				return null;
			}
		});
	}

}
