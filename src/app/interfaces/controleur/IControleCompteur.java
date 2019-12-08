package app.interfaces.controleur;

import fr.sorbonne_u.components.interfaces.RequiredI;

/**
 * Interface requise pour recuperer les consommations et productions globales du systeme
 * et permettre l'ajout d'appareil sur le compteur
 * 
 * @author Willy Nassim
 */
public interface IControleCompteur extends RequiredI {
	
	/**
	 * Permet au controleur de demander l'ajout d'un appareil sur le compteur
	 * @param uri
	 * @throws Exception
	 */
	public void demanderAjoutAppareil(String uri) throws Exception;
	
	/**
	 * Permet au controleur de demander l'ajout d'une unite de production sur le compteur
	 * @param uri
	 * @throws Exception
	 */
	public void demanderAjoutUniteProduction(String uri) throws Exception;
	
	/**
	 * Permet de récupérer la consommation totale des appareils depuis le compteur
	 * requiert un compteur
	 * @return
	 * @throws Exception
	 */
	public double getConsommationGlobale() throws Exception;
	
	/**
	 * Permet de récuperer la production totale des unités de production depuis le compteur
	 * requiert un compteur
	 * @return
	 * @throws Exception
	 */
	public double getProductionGlobale() throws Exception;
}
