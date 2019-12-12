package app.ports.lavelinge;

import app.components.LaveLinge;
import app.interfaces.generateur.IEntiteDynamique;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class LaveLingeAssembleurInPort extends AbstractInboundPort implements IEntiteDynamique {

	private static final long serialVersionUID = 1L;

	public LaveLingeAssembleurInPort(ComponentI owner) throws Exception {
		super(IEntiteDynamique.class, owner);
	}

	public LaveLingeAssembleurInPort(String uri, ComponentI owner) throws Exception {
		super(IEntiteDynamique.class, owner);
	}

	@Override
	public void demanderAjoutLogement(String uri) throws Exception {
		this.owner.handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((LaveLinge) this.getServiceOwner()).demandeAjoutControleur(uri);
				return null;
			}
		});
	}

}