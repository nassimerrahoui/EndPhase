package app.interfaces.controleur;

import fr.sorbonne_u.components.interfaces.RequiredI;

/**
 * Requiert des informations sur la consommation des appareils
 * ainsi la production de unités de productions depuis le compteur
 * 
 * @author Willy Nassim
 */
public interface IControleCompteur extends RequiredI {
	
	/**
	 * Permet de récupérer la consommation totale des appareils depuis le compteur
	 * requiert un compteur
	 * @return
	 * @throws Exception
	 */
	public double getAllConsommations() throws Exception;
	
	/**
	 * Permet de récuperer la production totale des unités de production depuis le compteur
	 * requiert un compteur
	 * @return
	 * @throws Exception
	 */
	public double getAllProductions() throws Exception;
}
