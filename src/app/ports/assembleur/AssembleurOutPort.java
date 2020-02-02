package app.ports.assembleur;

import app.interfaces.assembleur.IAssembleur;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;


/**
 * @author Willy Nassim
 */

public class AssembleurOutPort extends AbstractOutboundPort implements IAssembleur {

	private static final long serialVersionUID = 1L;

	public AssembleurOutPort(ComponentI owner) throws Exception {
		super(IAssembleur.class, owner);
	}
	
	public AssembleurOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, IAssembleur.class, owner);
	}

	@Override
	public void ajoutLogement(String uri) throws Exception {
		((IAssembleur) this.connector).ajoutLogement(uri);
	}

	@Override
	public void dynamicExecute() throws Exception {
		((IAssembleur) this.connector).dynamicExecute();
	}
}