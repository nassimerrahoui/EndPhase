package app.ports.lavelinge;

import app.interfaces.appareil.IConsommation;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

/**
 * @author Willy Nassim
 */
public class LaveLingeCompteurOutPort extends AbstractOutboundPort implements IConsommation {

	private static final long serialVersionUID = 1L;

	public LaveLingeCompteurOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, IConsommation.class, owner);
	}
	
	public LaveLingeCompteurOutPort(ComponentI owner) throws Exception {
		super(IConsommation.class, owner);
	}

	@Override
	public void envoyerConsommation(String uri, double consommation) throws Exception {
		((IConsommation) this.connector).envoyerConsommation(uri, consommation);
	}
}
