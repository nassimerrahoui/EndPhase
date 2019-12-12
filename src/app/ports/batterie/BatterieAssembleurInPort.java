package app.ports.batterie;

import app.components.Batterie;
import app.interfaces.generateur.IEntiteDynamique;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class BatterieAssembleurInPort extends AbstractInboundPort implements IEntiteDynamique {

	private static final long serialVersionUID = 1L;

	public BatterieAssembleurInPort(ComponentI owner) throws Exception {
		super(IEntiteDynamique.class, owner);
	}

	public BatterieAssembleurInPort(String uri, ComponentI owner) throws Exception {
		super(IEntiteDynamique.class, owner);
	}

	@Override
	public void demanderAjoutLogement(String uri) throws Exception {
		this.owner.handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((Batterie) this.getServiceOwner()).demandeAjoutControleur(uri);
				return null;
			}
		});
	}

}
