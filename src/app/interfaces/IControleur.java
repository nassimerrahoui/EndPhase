package app.interfaces;

import app.data.Message;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataRequiredI;

public interface IControleur extends DataOfferedI, DataRequiredI {
	public DataOfferedI.DataI getMessage(String uri) throws Exception;
	
	public void getEnergie(Message m) throws Exception;
}
