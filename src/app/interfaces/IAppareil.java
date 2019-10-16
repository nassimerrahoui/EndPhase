package app.interfaces;

import fr.sorbonne_u.components.interfaces.DataOfferedI;

public interface IAppareil extends DataOfferedI {

	public void recevoirMessage(DataI d) throws Exception;

}
