package app.connectors;

import fr.sorbonne_u.components.connectors.AbstractDataConnector;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataRequiredI.DataI;

public class AppareilServiceConnector extends AbstractDataConnector {

	@Override
	public DataI request() throws Exception {
		return null;
	}

	@Override
	public void send(DataOfferedI.DataI d) throws Exception {
		this.connect(offering, requiring);
	}

}
