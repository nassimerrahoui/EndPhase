package app.connectors;

import app.interfaces.generateur.IAssembleur;
import app.interfaces.generateur.IEntiteDynamique;
import fr.sorbonne_u.components.connectors.AbstractConnector;

public class AssembleurEntiteConnector extends AbstractConnector implements IAssembleur {

	@Override
	public void ajoutLogement(String uri) throws Exception {
		((IEntiteDynamique) this.offering).demanderAjoutLogement(uri);
	}

}
