package app.connectors;

import app.interfaces.assembleur.IAssembleur;
import app.interfaces.assembleur.IComposantDynamique;
import fr.sorbonne_u.components.connectors.AbstractConnector;

/**
 * @author Willy Nassim
 */

public class AssembleurEntiteConnector extends AbstractConnector implements IAssembleur {

	@Override
	public void ajoutLogement(String uri) throws Exception {
		((IComposantDynamique) this.offering).demanderAjoutLogement(uri);
	}

	@Override
	public void dynamicExecute() throws Exception {
		((IComposantDynamique) this.offering).dynamicExecute();
	}
}
