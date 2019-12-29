package app.interfaces.controleur;

import app.util.ModeAspirateur;
import fr.sorbonne_u.components.interfaces.RequiredI;

/**
 * Interface requise pour que le controleur envoi une ou plusieurs actions a l'aspirateur.
 * 
 * @author Willy Nassim
 *
 */
public interface IControleAspirateur extends RequiredI {
	
	/**
	 * Permet de changer le mode de l'aspirateur
	 * requiert au moins un appareil
	 * @param etat
	 * @throws Exception
	 */
	public void envoyerModeAspirateur(ModeAspirateur etat) throws Exception;
}
