package app.interfaces.appareil;

import java.util.ArrayList;

import app.util.ModeLaveLinge;
import app.util.TemperatureLaveLinge;
import fr.sorbonne_u.components.interfaces.OfferedI;

/**
 * Permet au lave-linge de proposer une interface de controle pour le controleur
 * @author Willy Nassim
 *
 */
public interface ILaveLinge extends OfferedI {

	/**
	 * Permet de changer l'etat du lave-linge
	 * @param etat
	 * @throws Exception
	 */
	public void setModeLaveLinge(ModeLaveLinge etat) throws Exception;
	
	/**
	 * Enclenche le cycle du lave-linge (lavage, rincage, essorage) a une heure et minutes donnee
	 * @param heure
	 * @param minutes
	 * @throws Exception
	 */
	public void planifierCycle(ArrayList<ModeLaveLinge> planification, int heure, int minutes) throws Exception;
	
	/**
	 * Permet de definir la temperature des prochains lavages
	 * @param temperature
	 */
	public void setTemperature(TemperatureLaveLinge tl) throws Exception;
	
}
