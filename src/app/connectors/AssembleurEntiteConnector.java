package app.connectors;

import app.interfaces.generateur.IAssembleur;
import app.interfaces.generateur.IComposantDynamique;
import fr.sorbonne_u.components.connectors.AbstractConnector;

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
