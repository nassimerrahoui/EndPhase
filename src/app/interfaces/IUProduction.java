package app.interfaces;

import app.data.Message;
import fr.sorbonne_u.components.interfaces.DataRequiredI;

public interface IUProduction extends DataRequiredI {

	public void recevoirMessage(Message m) throws Exception;

}
