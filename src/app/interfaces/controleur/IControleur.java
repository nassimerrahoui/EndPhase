package app.interfaces.controleur;

import app.util.EtatAppareil;
import app.util.EtatUniteProduction;
import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

/**
 * Offre une interface pour que les appareils s'ajoute au systeme.
 * 
 * Requiert des informations sur la consommation des appareils
 * ainsi la production de unit�s de productions depuis le compteur
 * 
 * Requiert des appareils pour envoyer des actions.
 * 
 * @author Willy Nassim
 *
 */
public interface IControleur extends OfferedI, RequiredI {

	/**
	 * Permet d'offrir l'ajout d'un appareil a la liste des appareils du controleur
	 * @param uri
	 * @throws Exception
	 */
	public void ajouterAppareil(String uri) throws Exception;
	
	/**
	 * Permet d'offrir l'ajout d'une unite de production a la liste des unite de production du controleur
	 * @param uri
	 * @throws Exception
	 */
	public void ajouterUniteProduction(String uri) throws Exception;
	
	/**
	 * Permet d'allumer ou eteindre un appareil
	 * requiert au moins un appareil
	 * @param etat
	 * @throws Exception
	 */
	public void envoyerEtatAppareil(EtatAppareil etat) throws Exception;
	
	/**
	 * Permet d'allumer ou eteindre une unite de production
	 * requiert au moins une unite de production
	 * @param etat
	 * @throws Exception
	 */
	public void envoyerEtatUniteProduction(EtatUniteProduction etat) throws Exception;
	
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
