package app.interfaces.controleur;

import app.util.EtatUniteProduction;
import fr.sorbonne_u.components.interfaces.RequiredI;

/**
 * Interface requise pour allumer ou eteindre la batterie
 * 
 * @author Willy Nassim
 *
 */
public interface IControleBatterie extends RequiredI {

	/**
	 * Permet d'allumer ou eteindre la batterie
	 * @param etat
	 * @throws Exception
	 */
	public void envoyerEtatUniteProduction(EtatUniteProduction etat) throws Exception;
}
