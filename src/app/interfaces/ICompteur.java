package app.interfaces;

import app.data.Message;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataRequiredI;

public interface ICompteur extends DataOfferedI, DataRequiredI {
	
	public void recevoirMessage(Message m) throws Exception;

	public DataOfferedI.DataI getConsommation() throws Exception;
}
