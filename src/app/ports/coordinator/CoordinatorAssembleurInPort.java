package app.ports.coordinator;

import app.components.Coordinator;
import app.interfaces.assembleur.IComposantDynamique;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class CoordinatorAssembleurInPort extends AbstractInboundPort implements IComposantDynamique {

	private static final long serialVersionUID = 1L;

	public CoordinatorAssembleurInPort(ComponentI owner) throws Exception {
		super(IComposantDynamique.class, owner);
	}

	public CoordinatorAssembleurInPort(String uri, ComponentI owner) throws Exception {
		super(IComposantDynamique.class, owner);
	}

	@Override
	public void demanderAjoutLogement(String uri) throws Exception {
		// le coordinator ne fait pas parti des appareils ni des unites de productions
	}
	
	@Override
	public void dynamicExecute() throws Exception {
		this.owner.handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((Coordinator) this.getServiceOwner()).dynamicExecute();
				return null;
			}
		});
	}
}
