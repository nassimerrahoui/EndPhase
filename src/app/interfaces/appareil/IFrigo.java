package app.interfaces.appareil;

import app.util.ModeFrigo;

/**
 * Permet au frigo de proposer une interface de controle pour le controleur
 * @author Willy Nassim
 *
 */
public interface IFrigo extends IAppareil {
	
	/**
	 * Permet d'etablir la temperature a atteindre pour le refrigerateur 
	 * @param temperature
	 */
	public void setTemperature_Refrigerateur(double temperature) throws Exception;
	
	/**
	 * Permet d'etablir la temperature a atteindre pour le congelateur
	 * @param temperature
	 */
	public void setTemperature_Congelateur(double temperature) throws Exception;
	
	/**
	 * Permet d'allumer ou eteindre la lumiere du refrigerateur
	 * @param mf
	 */
	public void setLumiere_Refrigerateur(ModeFrigo mf) throws Exception;

	/**
	 * Permet d'allumer ou eteindre la lumiere du congelateur
	 * @param mf
	 */
	public void setLumiere_Congelateur(ModeFrigo mf) throws Exception;
}
