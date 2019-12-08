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
	 * Recupere la consommation d'un appareil
	 * @return
	 * @throws Exception
	 */
	public double getAppareilConsommation() throws Exception;
	
	/**
	 * Recupere la production d'energie d'une unite de production
	 * @return
	 * @throws Exception
	 */
	public double getUniteProduction() throws Exception;
	
}
