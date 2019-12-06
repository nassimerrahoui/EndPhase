package app.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;


public interface IControleur extends OfferedI {

	/**
	 * Permet a un appareil de s'ajouter a la liste des appareils du controleur
	 * @param uri
	 * @throws Exception
	 */
	public void ajouterAppareil(String uri) throws Exception;
	
}
