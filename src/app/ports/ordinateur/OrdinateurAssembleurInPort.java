package app.ports.ordinateur;

import app.components.Ordinateur;
import app.interfaces.generateur.IComposantDynamique;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class OrdinateurAssembleurInPort extends AbstractInboundPort implements IComposantDynamique {

	private static final long serialVersionUID = 1L;

	public OrdinateurAssembleurInPort(ComponentI owner) throws Exception {
		super(IComposantDynamique.class, owner);
	}

	public OrdinateurAssembleurInPort(String uri, ComponentI owner) throws Exception {
		super(IComposantDynamique.class, owner);
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
	
	@Override
	public void dynamicExecute() throws Exception {
		this.owner.handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((Ordinateur) this.getServiceOwner()).dynamicExecute();
				return null;
			}
		});
	}
}
