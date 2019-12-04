package app.interfaces;

import app.util.ModeOrdinateur;

public interface IOrdinateur extends IAppareil {

	/**
	 * Permet de definir le mode de consommation electrique de l'ordinateur
	 * @param mo
	 * @throws Exception
	 */
	public void setMode(ModeOrdinateur mo) throws Exception;
}
