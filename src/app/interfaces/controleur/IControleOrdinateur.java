package app.interfaces.controleur;

import app.util.EtatAppareil;
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
	 * Permet d'allumer ou eteindre un appareil
	 * requiert au moins un appareil
	 * @param etat
	 * @throws Exception
	 */
	public void envoyerEtatAppareil(EtatAppareil etat) throws Exception;
	
	/**
	 * Permet de definir le mode de consommation electrique de l'ordinateur
	 * @param mo
	 * @throws Exception
	 */
	public void envoyerMode(ModeOrdinateur mo) throws Exception;
}
