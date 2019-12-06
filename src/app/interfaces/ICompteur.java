package app.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;

public interface ICompteur extends OfferedI {
	
	/**
	 * Retourne la consommation de tout les appareils mesure par le compteur
	 * @return
	 * @throws Exception
	 */
	public double getAllConsommations() throws Exception;
	
	/**
	 * Permet a un appareil de s'ajouter a la liste des appareils du compteur
	 * @param uri
	 * @throws Exception
	 */
	public void ajouterAppareil(String uri) throws Exception;
	
	/**
	 * Retourne la production d'energie globale des unites de productions
	 */
	public void getAllProductions() throws Exception;
}
