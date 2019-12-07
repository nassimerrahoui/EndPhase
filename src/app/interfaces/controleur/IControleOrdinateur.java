package app.interfaces.controleur;

import app.util.ModeOrdinateur;
import fr.sorbonne_u.components.interfaces.RequiredI;

/**
 * Interface requise pour que le controleur envoi une ou plusieurs actions a l'ordinateur.
 * 
 * @author Willy Nassim
 *
 */
public interface IControleOrdinateur extends RequiredI {
	
	/**
	 * Permet de definir le mode de consommation electrique de l'ordinateur
	 * @param mo
	 * @throws Exception
	 */
	public void envoyerMode(ModeOrdinateur mo) throws Exception;
}
