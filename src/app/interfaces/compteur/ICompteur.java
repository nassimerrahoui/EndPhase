package app.interfaces.compteur;

import fr.sorbonne_u.components.interfaces.RequiredI;

/**
 * Interface requise pour recuperer la consommation des appareils
 * et recuperer la production des unites de production
 * @author Willy Nassim
 *
 */
public interface ICompteur extends RequiredI {
	
	/**
	 * Recupere la consommation de l'appareil
	 * @return
	 * @throws Exception
	 */
	public double getFrigoConsommation() throws Exception;
	
	/**
	 * Recupere la consommation de l'appareil
	 * @return
	 * @throws Exception
	 */
	public double getLaveLingeConsommation() throws Exception;
	
	/**
	 * Recupere la consommation de l'appareil
	 * @return
	 * @throws Exception
	 */
	public double getOrdinateurConsommation() throws Exception;
	
	/**
	 * Recupere la production d'energie de l'unite de production
	 * @return
	 * @throws Exception
	 */
	public double getPanneauProduction() throws Exception;
	
	/**
	 * Recupere la production d'energie de l'unite de production
	 * @return
	 * @throws Exception
	 */
	public double getBatterieProduction() throws Exception;
}
