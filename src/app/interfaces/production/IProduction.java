package app.interfaces.production;

import fr.sorbonne_u.components.interfaces.RequiredI;

/**
 * Interface requise par les unites de production pour le compteur
 * @author Willy Nassim
 *
 */
public interface IProduction extends RequiredI {
	
	/**
	 * Envoie la production electrique d'une unite de production au compteur
	 * @return
	 * @throws Exception
	 */
	public void envoyerProduction(String uri, double production) throws Exception;
}
