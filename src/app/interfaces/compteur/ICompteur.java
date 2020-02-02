package app.interfaces.compteur;

import fr.sorbonne_u.components.interfaces.OfferedI;

/**
 * Interface offerte pour recuperer la consommation des appareils
 * et recuperer la production des unites de production
 * @author Willy Nassim
 *
 */
public interface ICompteur extends OfferedI {
	
	/**
	 * Recupere la consommation de l'appareil
	 * @throws Exception
	 */
	public void setAppareilConsommation(String uri, double consommation) throws Exception;
	
	/**
	 * Recupere la production d'energie de l'unite de production
	 * @throws Exception
	 */
	public void setUniteProduction(String uri, double production) throws Exception;
}
