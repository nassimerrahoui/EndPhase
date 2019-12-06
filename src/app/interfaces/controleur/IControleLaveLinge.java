package app.interfaces.controleur;

import app.util.ModeLaveLinge;
import app.util.TemperatureLaveLinge;

/**
 * Permet au controleur d'envoyer une ou plusieurs actions au lave-linge
 * 
 * @author Willy Nassim
 *
 */
public interface IControleLaveLinge extends IControleur {
	
	/**
	 * Enclenche le cycle du lave-linge (lavage, rincage, essorage) a une heure donnee
	 * @param heure
	 * @throws Exception
	 */
	public void envoyerPlanificationCycle(double heure) throws Exception;
	
	/**
	 * Enclenche un seul mode du lave-linge a une heure donnee
	 * @param ml
	 * @param heure
	 * @throws Exception
	 */
	public void envoyerPlanificationMode(ModeLaveLinge ml, double heure) throws Exception;
	
	/**
	 * Permet de definir la temperature des prochains lavages
	 * @param temperature
	 */
	public void envoyerTemperature(TemperatureLaveLinge tl) throws Exception;
	
}
