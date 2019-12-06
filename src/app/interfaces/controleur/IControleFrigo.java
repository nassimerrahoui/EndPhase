package app.interfaces.controleur;

import app.util.ModeFrigo;

/**
 * Permet au controleur d'envoyer une ou plusieurs actions au frigo
 * 
 * @author Willy Nassim
 *
 */
public interface IControleFrigo extends IControleur {

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
	
	/**
	 * Permet d'allumer ou eteindre la lumiere du refrigerateur
	 * @param mf
	 */
	public void envoyerLumiere_Refrigerateur(ModeFrigo mf) throws Exception;

	/**
	 * Permet d'allumer ou eteindre la lumiere du congelateur
	 * @param mf
	 */
	public void envoyerLumiere_Congelateur(ModeFrigo mf) throws Exception;
}
