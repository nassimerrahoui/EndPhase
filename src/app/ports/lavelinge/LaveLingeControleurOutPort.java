package app.ports.lavelinge;

import app.interfaces.appareil.IAjoutAppareil;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class LaveLingeControleurOutPort extends AbstractOutboundPort implements IAjoutAppareil {

	private static final long serialVersionUID = 1L;

	public LaveLingeControleurOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, IAjoutAppareil.class, owner);
	}
	
	public LaveLingeControleurOutPort(ComponentI owner) throws Exception {
		super(IAjoutAppareil.class, owner);
	}

	@Override
	public void demandeAjoutControleur(String uri) throws Exception {
		((LaveLingeControleurOutPort) this.connector).demandeAjoutControleur(uri);
	}
}
