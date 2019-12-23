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
	 * Permet de changer le mode de l'ordinateur
	 * requiert au moins un appareil
	 * @param etat
	 * @throws Exception
	 */
	public void envoyerModeOrdinateur(ModeOrdinateur etat) throws Exception;
}
