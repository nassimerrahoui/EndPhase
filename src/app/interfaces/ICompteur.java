package app.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

public interface ICompteur extends OfferedI, RequiredI {
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public double getConsommation() throws Exception;
}
