package app.interfaces.controleur;

import fr.sorbonne_u.components.interfaces.RequiredI;

/**
 * Requiert des informations sur la consommation des appareils
 * ainsi la production de unit�s de productions depuis le compteur
 * 
 * @author Willy Nassim
 */
public interface IControleCompteur extends RequiredI {
	
	/**
	 * Permet de r�cup�rer la consommation totale des appareils depuis le compteur
	 * requiert un compteur
	 * @return
	 * @throws Exception
	 */
	public double getAllConsommations() throws Exception;
	
	/**
	 * Permet de r�cuperer la production totale des unit�s de production depuis le compteur
	 * requiert un compteur
	 * @return
	 * @throws Exception
	 */
	public double getAllProductions() throws Exception;
}
