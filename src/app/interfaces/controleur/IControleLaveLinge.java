package app.interfaces.controleur;

import java.util.ArrayList;

import app.util.ModeLaveLinge;
import app.util.TemperatureLaveLinge;
import fr.sorbonne_u.components.interfaces.RequiredI;

/**
 * Permet au controleur d'envoyer une ou plusieurs actions au lave-linge
 * 
 * @author Willy Nassim
 *
 */
public interface IControleLaveLinge extends RequiredI {
	
	/**
	 * Permet de changer le mode du lave-linge
	 * requiert au moins un appareil
	 * @param etat
	 * @throws Exception
	 */
	public void envoyerModeLaveLinge(ModeLaveLinge etat) throws Exception;
	
	/**
	 * Enclenche le cycle du lave-linge (lavage, rincage, essorage) a une heure donnee
	 * @param heure
	 * @throws Exception
	 */
	public void envoyerPlanificationCycle(ArrayList<ModeLaveLinge> planification, int heure, int minutes) throws Exception;
	
	/**
	 * Permet de definir la temperature des prochains lavages
	 * @param temperature
	 */
	public void envoyerTemperature(TemperatureLaveLinge temperature) throws Exception;
	
}
