package app.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;

public interface IProduction extends OfferedI{
	/**
	 * Retourne la production electrique d'une unite de production
	 * @return
	 * @throws Exception
	 */
	public double getProduction() throws Exception;
}
