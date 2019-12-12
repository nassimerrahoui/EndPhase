package app.interfaces.generateur;

import fr.sorbonne_u.components.interfaces.OfferedI;

/**
 * Interface offerte par les appareils ou les unites de production
 * qui ont besoin d'integrer le systeme dynamiquement par l'assembleur
 * 
 * @author Willy Nassim
 *
 */
public interface IEntiteDynamique extends OfferedI {

	/**
	 * Permet l'assembleur d'ajouter un appareil ou une unite de production
	 * au systeme en passant par le controleur
	 * @throws Exception 
	 */
	public void demanderAjoutLogement(String uri) throws Exception;
}
