package app.ports.aspirateur;

import app.interfaces.appareil.IAjoutAppareil;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class AspirateurControleurOutPort extends AbstractOutboundPort implements IAjoutAppareil {

	private static final long serialVersionUID = 1L;

	public AspirateurControleurOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, IAjoutAppareil.class, owner);
	}
	
	public AspirateurControleurOutPort(ComponentI owner) throws Exception {
		super(IAjoutAppareil.class, owner);
	}
	
	public void demandeAjoutControleur(String uri) throws Exception {
		((IAjoutAppareil) this.connector).demandeAjoutControleur(uri);
	}
}
