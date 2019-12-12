package app.interfaces.generateur;

import fr.sorbonne_u.components.interfaces.RequiredI;

/**
 * Interface requise par l'assembleur pour integrer 
 * les appareils et les unites de productions au logement
 * 
 * @author Willy Nassim
 *
 */
public interface IAssembleur extends RequiredI {

	/**
	 * Permet a un appareil ou une unite de production
	 * qui implante l'interface entite dynamique
	 * d'integrer le logement en passant par le controleur
	 * @throws Exception 
	 */
	public void ajoutLogement(String uri) throws Exception;
}
