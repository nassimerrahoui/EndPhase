package app.ports.aspirateur;

import app.interfaces.appareil.IAjoutAppareil;
import app.util.TypeAppareil;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

/**
 * @author Willy Nassim
 */

public class AspirateurControleurOutPort extends AbstractOutboundPort implements IAjoutAppareil {

	private static final long serialVersionUID = 1L;

	public AspirateurControleurOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, IAjoutAppareil.class, owner);
	}
	
	public AspirateurControleurOutPort(ComponentI owner) throws Exception {
		super(IAjoutAppareil.class, owner);
	}
	
	public void demandeAjoutControleur(String uri, String classe, TypeAppareil type) throws Exception {
		((IAjoutAppareil) this.connector).demandeAjoutControleur(uri, classe, type);
	}
}
