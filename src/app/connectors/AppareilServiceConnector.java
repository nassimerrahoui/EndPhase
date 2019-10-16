package app.connectors;

import app.interfaces.IAppareil;
import app.interfaces.IControleur;
import fr.sorbonne_u.components.connectors.AbstractDataConnector;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataRequiredI;

public class AppareilServiceConnector extends AbstractDataConnector {

	@Override
	public void send(DataOfferedI.DataI d) throws Exception {
		((IAppareil.PushI) this.requiring).receive(this.offered2required(d)) ;
	}

	@Override
	public DataRequiredI.DataI request() throws Exception {
		return this.offered2required(((IControleur.PullI)this.offering).get()) ;
	}

}
