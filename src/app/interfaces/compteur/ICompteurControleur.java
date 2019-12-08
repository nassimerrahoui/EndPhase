package app.interfaces.compteur;

import fr.sorbonne_u.components.interfaces.OfferedI;

/**
 * Permet au compteur d'offrir une interface au controleur
 * @author Willy Nassim
 *
 */
public interface ICompteurControleur extends OfferedI {
	
	/**
	 * Permet au controleur de demander l'ajout d'un appareil sur le compteur
	 * @param uri
	 * @throws Exception
	 */
	public void ajouterAppareil(String uri) throws Exception;
	
	/**
	 * Permet au controleur de demander l'ajout d'une unite de production sur le compteur
	 * @param uri
	 * @throws Exception
	 */
	public void ajouterUniteProduction(String uri) throws Exception;
	
	/**
	 * Permet de recuperer la consommation totale des appareils depuis le compteur
	 * @return
	 * @throws Exception
	 */
	public double envoyerConsommationGlobale() throws Exception;
	
	/**
	 * Permet de récuperer la production totale des unites de production depuis le compteur
	 * @return
	 * @throws Exception
	 */
	public double envoyerProductionGlobale() throws Exception;
}
