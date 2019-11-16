package app.interfaces;

import app.data.Message;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataRequiredI;

public interface IUProduction extends DataRequiredI, DataOfferedI {

	public void recevoirMessage(Message m) throws Exception;
	
	public DataOfferedI.DataI getProduction() throws Exception;

}
