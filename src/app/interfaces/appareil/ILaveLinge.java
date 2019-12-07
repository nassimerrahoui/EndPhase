package app.interfaces.appareil;

import app.util.ModeLaveLinge;
import app.util.TemperatureLaveLinge;

/**
 * Permet au lave-linge de proposer une interface de controle pour le controleur
 * @author Willy Nassim
 *
 */
public interface ILaveLinge extends IAppareil {

	/**
	 * Enclenche le cycle du lave-linge (lavage, rincage, essorage) a une heure et minutes donnee
	 * @param heure
	 * @param minutes
	 * @throws Exception
	 */
	public void planifierCycle(int heure, int minutes) throws Exception;
	
	/**
	 * Enclenche un seul mode du lave-linge a une heure et minutes donnee
	 * @param ml
	 * @param heure
	 * @param minutes
	 * @throws Exception
	 */
	public void planifierMode(ModeLaveLinge ml, int heure, int minutes) throws Exception;
	
	/**
	 * Permet de definir la temperature des prochains lavages
	 * @param temperature
	 */
	public void setTemperature(TemperatureLaveLinge tl) throws Exception;
	
}