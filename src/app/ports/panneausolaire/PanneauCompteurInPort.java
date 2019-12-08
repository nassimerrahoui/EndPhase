package app.ports.panneausolaire;

import app.components.PanneauSolaire;
import app.interfaces.production.IProduction;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class PanneauCompteurInPort extends AbstractInboundPort implements IProduction {
	private static final long serialVersionUID = 1L;

	public PanneauCompteurInPort(String uri, ComponentI owner) throws Exception {
		super(uri, IProduction.class, owner);
	}
	
	public PanneauCompteurInPort(ComponentI owner) throws Exception {
		super(IProduction.class, owner);
	}

	@Override
	public double envoyerProduction() throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((PanneauSolaire) owner).getProduction());
	}
}
