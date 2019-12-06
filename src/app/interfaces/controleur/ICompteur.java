package app.interfaces.controleur;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

/**
 * Permet au compteur d'offrir une interface au controleur
 * Requiert les consommations des appareils
 * @author Willy Nassim
 *
 */
public interface ICompteur extends OfferedI, RequiredI {
	
	/**
	 * Permet au controleur de demander l'ajout d'un appareil sur le compteur
	 * @param uri
	 * @throws Exception
	 */
	public void ajouterAppareil(String uri) throws Exception;
	
	/**
	 * Envoi la consommation de tout les appareils au controleur
	 * @return
	 * @throws Exception
	 */
	public double sendAllConsommations();
	
	/**
	 * Envoi la production de toutes les unités de production au controleur
	 * @return
	 * @throws Exception
	 */
	public double sendAllProductions() throws Exception;
	
	/**
	 * Recupere la consommation d'un appareil
	 * Requiert un appareil
	 * @return
	 * @throws Exception
	 */
	public double getAppareilConsommation() throws Exception;
	
	/**
	 * Recupere la production d'energie globale d'une unite de production
	 * Requiert un appareil
	 * @return
	 * @throws Exception
	 */
	public double getUniteProduction() throws Exception;
	
}
