package app.ports.frigo;

import app.interfaces.appareil.IAjoutAppareil;
import app.util.TypeAppareil;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;


/**
 * @author Willy Nassim
 */
public class FrigoControleurOutPort extends AbstractOutboundPort implements IAjoutAppareil {

	private static final long serialVersionUID = 1L;

	public FrigoControleurOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, IAjoutAppareil.class, owner);
	}
	
	public FrigoControleurOutPort(ComponentI owner) throws Exception {
		super(IAjoutAppareil.class, owner);
	}

	@Override
	public void demandeAjoutControleur(String uri, String classe, TypeAppareil type) throws Exception {
		((IAjoutAppareil) this.connector).demandeAjoutControleur(uri, classe, type);
	}
}
