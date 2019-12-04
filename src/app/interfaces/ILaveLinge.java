package app.interfaces;

import app.util.ModeLaveLinge;
import app.util.TemperatureLaveLinge;

public interface ILaveLinge extends IAppareil {

	/**
	 * Enclenche le cycle du lave-linge (lavage, rincage, essorage) a une heure donnee
	 * @param heure
	 * @throws Exception
	 */
	public void planifierCycle(double heure) throws Exception;
	
	/**
	 * Enclenche un seul mode du lave-linge a une heure donnee
	 * @param ml
	 * @param heure
	 * @throws Exception
	 */
	public void planifierMode(ModeLaveLinge ml, double heure) throws Exception;
	
	/**
	 * Permet de definir la temperature des prochains lavages
	 * @param temperature
	 */
	public void setTemperature(TemperatureLaveLinge tl) throws Exception;
	
}
