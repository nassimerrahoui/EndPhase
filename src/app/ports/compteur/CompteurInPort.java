package app.ports.compteur;

import app.components.Compteur;
import app.interfaces.compteur.ICompteurControleur;
import app.util.URI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class CompteurInPort extends AbstractInboundPort implements ICompteurControleur {

	private static final long serialVersionUID = 1L;

	public CompteurInPort(String uri, ComponentI owner) throws Exception {
		super(uri, ICompteurControleur.class, owner);
	}
	
	public CompteurInPort(ComponentI owner) throws Exception {
		super(ICompteurControleur.class, owner);
	}

	@Override
	public void ajouterAppareil(String uri) throws Exception {
		AbstractComponent.AbstractService<Void> task = new AbstractComponent.AbstractService<Void>() {
			public Void call() throws Exception {
				((Compteur) owner).ajouterAppareil(uri);
				return null;
			}
		};

		this.owner.handleRequestAsync(URI.POOL_CONTROLE_COMPTEUR_URI.getURI(), task);
	}

	@Override
	public void ajouterUniteProduction(String uri) throws Exception {
		AbstractComponent.AbstractService<Void> task = new AbstractComponent.AbstractService<Void>() {
			public Void call() throws Exception {
				((Compteur) owner).ajouterUniteProduction(uri);
				return null;
			}
		};

		this.owner.handleRequestAsync(URI.POOL_CONTROLE_COMPTEUR_URI.getURI(), task);
	}

	@Override
	public double envoyerConsommationGlobale() throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((Compteur) owner).envoyerConsommationGlobale());
	}

	@Override
	public double envoyerProductionGlobale() throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((Compteur) owner).envoyerProductionGlobale());
	}

}
