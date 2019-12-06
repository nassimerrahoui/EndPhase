package app.interfaces.controleur;

import app.util.ModeOrdinateur;

/**
 * Permet au controleur d'envoyer une ou plusieurs actions a l'ordinateur
 * 
 * @author Willy Nassim
 *
 */
public interface IControleOrdinateur extends IControleur {
	
	/**
	 * Permet de definir le mode de consommation electrique de l'ordinateur
	 * @param mo
	 * @throws Exception
	 */
	public void envoyerMode(ModeOrdinateur mo) throws Exception;
}
