package app.interfaces.controleur;

import app.util.ModeFrigo;
import fr.sorbonne_u.components.interfaces.RequiredI;

/**
 * Interface requise pour que le controleur envoie une ou plusieurs actions au frigo
 * 
 * @author Willy Nassim
 *
 */
public interface IControleFrigo extends RequiredI {
	
	/**
	 * Permet de changer le mode du frigo
	 * @param etat
	 * @throws Exception
	 */
	public void envoyerModeFrigo(ModeFrigo etat) throws Exception;

	/**
	 * Permet d'etablir la temperature a atteindre pour le refrigerateur 
	 * @param temperature
	 */
	public void envoyerTemperature_Refrigerateur(double temperature) throws Exception;
	
	/**
	 * Permet d'etablir la temperature a atteindre pour le congelateur
	 * @param temperature
	 */
	public void envoyerTemperature_Congelateur(double temperature) throws Exception;
}
