package app.interfaces.assembleur;

import fr.sorbonne_u.components.interfaces.RequiredI;

/**
 * Interface requise par l'assembleur pour :
 * - integrer les composants au logement
 * - lancer des taches dynamiquement sur ces composants
 * @author Willy Nassim
 *
 */
public interface IAssembleur extends RequiredI {

	/**
	 * Permet a un composant qui implante IComposantDynamique
	 * d'integrer le logement en passant par le controleur
	 * @throws Exception 
	 */
	public void ajoutLogement(String uri) throws Exception;
	
	
	/**
	 * Permet de lancer des taches dynamiquement sur un composant qui
	 * implante IComposantDynamique
	 * @throws Exception
	 */
	public void dynamicExecute() throws Exception;
}
