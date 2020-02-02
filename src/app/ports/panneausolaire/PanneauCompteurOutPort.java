package app.ports.panneausolaire;

import app.interfaces.production.IProduction;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;


/**
 * @author Willy Nassim
 */

public class PanneauCompteurOutPort extends AbstractOutboundPort implements IProduction {
	private static final long serialVersionUID = 1L;

	public PanneauCompteurOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, IProduction.class, owner);
	}
	
	public PanneauCompteurOutPort(ComponentI owner) throws Exception {
		super(IProduction.class, owner);
	}

	@Override
	public void envoyerProduction(String uri, double production) throws Exception {
		((IProduction) this.connector).envoyerProduction(uri, production);
	}
}
