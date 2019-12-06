package app.interfaces.appareil;

import app.util.ModeOrdinateur;

/**
 * Permet a l'ordinateur de proposer une interface de controle pour leur controleur
 * @author Willy Nassim
 *
 */
public interface IOrdinateur extends IAppareil {

	/**
	 * Permet de definir le mode de consommation electrique de l'ordinateur
	 * @param mo
	 * @throws Exception
	 */
	public void setMode(ModeOrdinateur mo) throws Exception;
}
