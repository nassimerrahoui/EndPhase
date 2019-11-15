package app.connectors;

import fr.sorbonne_u.components.connectors.AbstractDataConnector;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataRequiredI;

public class DataServiceConnector extends AbstractDataConnector {

	@Override
	public void send(DataOfferedI.DataI d) throws Exception {
		((DataRequiredI.PushI) this.requiring).receive(this.offered2required(d)) ;
	}

	@Override
	public DataRequiredI.DataI request() throws Exception {
		return this.offered2required(((DataOfferedI.PullI)this.offering).get()) ;
	}

}
