package app.interfaces.controleur;

import app.util.EtatUniteProduction;
import fr.sorbonne_u.components.interfaces.RequiredI;

/**
 * Interface requise pour allumer ou eteindre le panneau solaire
 * 
 * @author Willy Nassim
 *
 */
public interface IControlePanneau extends RequiredI {

	/**
	 * Permet d'allumer ou eteindre le panneau solaire
	 * @param etat
	 * @throws Exception
	 */
	public void envoyerEtatUniteProduction(EtatUniteProduction etat) throws Exception;
}
