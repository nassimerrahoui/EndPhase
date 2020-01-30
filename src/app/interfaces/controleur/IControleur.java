package app.interfaces.controleur;

import app.util.TypeAppareil;
import fr.sorbonne_u.components.interfaces.OfferedI;

/**
 * Offre une interface pour que les appareils s'ajoutent au systeme.
 * 
 * @author Willy Nassim
 *
 */
public interface IControleur extends OfferedI {

	/**
	 * Permet d'offrir l'ajout d'un appareil a la liste des appareils du controleur
	 * @param uri
	 * @throws Exception
	 */
	public void ajouterAppareil(String uri, String className, TypeAppareil type) throws Exception;
	
	/**
	 * Permet d'offrir l'ajout d'une unite de production a la liste des unite de production du controleur
	 * @param uri
	 * @throws Exception
	 */
	public void ajouterUniteProduction(String uri) throws Exception;

}