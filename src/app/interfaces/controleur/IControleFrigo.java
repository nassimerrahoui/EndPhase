package app.interfaces.controleur;

import app.util.EtatAppareil;
import app.util.ModeFrigo;
import fr.sorbonne_u.components.interfaces.RequiredI;

/**
 * Interface requise pour que le controleur envoi une ou plusieurs actions au frigo
 * 
 * @author Willy Nassim
 *
 */
public interface IControleFrigo extends RequiredI {
	
	/**
	 * Permet d'allumer ou eteindre un appareil
	 * @param etat
	 * @throws Exception
	 */
	public void envoyerEtatAppareil(EtatAppareil etat) throws Exception;

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
