package app.ports.ordi;

import app.interfaces.appareil.IAjoutAppareil;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class OrdinateurControleurOutPort extends AbstractOutboundPort implements IAjoutAppareil {

	private static final long serialVersionUID = 1L;

	public OrdinateurControleurOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, IAjoutAppareil.class, owner);
	}
	
	public OrdinateurControleurOutPort(ComponentI owner) throws Exception {
		super(IAjoutAppareil.class, owner);
	}
	
	public void demandeAjoutControleur(String uri) {
		((OrdinateurControleurOutPort) this.connector).demandeAjoutControleur(uri);
	}
}
