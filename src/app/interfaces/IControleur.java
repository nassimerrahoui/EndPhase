package app.interfaces;

import fr.sorbonne_u.components.interfaces.DataOfferedI;

public interface IControleur extends DataOfferedI {
	public DataI getMessage(String uri) throws Exception;
}
