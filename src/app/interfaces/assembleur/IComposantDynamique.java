package app.interfaces.assembleur;

import fr.sorbonne_u.components.interfaces.OfferedI;

/**
 * Interface offerte par les composants
 * qui ont besoin d'integrer le systeme dynamiquement par l'assembleur
 * 
 * @author Willy Nassim
 *
 */
public interface IComposantDynamique extends OfferedI {

	/**
	 * Permet a un composant de demander un ajout au systeme en passant par le controleur
	 * @throws Exception 
	 */
	public void demanderAjoutLogement(String uri) throws Exception;
	
	/**
	 * Permet a un composant de lancer des taches
	 * @throws Exception
	 */
	public void dynamicExecute() throws Exception;
}
