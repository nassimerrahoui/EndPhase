package app.interfaces;

import app.data.Message;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataRequiredI;

public interface IAppareil extends DataRequiredI, DataOfferedI{

	public void recevoirMessage(Message m) throws Exception;

	public DataOfferedI.DataI getConsommation() throws Exception;

}
