package app.ports.compteur;

import app.components.Compteur;
import app.interfaces.assembleur.IComposantDynamique;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class CompteurAssembleurInPort extends AbstractInboundPort implements IComposantDynamique {

	private static final long serialVersionUID = 1L;

	public CompteurAssembleurInPort(ComponentI owner) throws Exception {
		super(IComposantDynamique.class, owner);
	}

	public CompteurAssembleurInPort(String uri, ComponentI owner) throws Exception {
		super(IComposantDynamique.class, owner);
	}

	@Override
	public void demanderAjoutLogement(String uri) throws Exception {
		// Aucune tache a effectuer car c'est le compteur
		// todo separer les interface dynamique pour les appareils/unites
		// de ceux pour le controleur et le compteur
	}

	@Override
	public void dynamicExecute() throws Exception {
		this.owner.handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((Compteur) this.getServiceOwner()).dynamicExecute();
				return null;
			}
		});
	}

}
