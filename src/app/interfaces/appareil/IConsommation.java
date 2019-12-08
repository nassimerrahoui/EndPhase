package app.interfaces.appareil;

import fr.sorbonne_u.components.interfaces.RequiredI;

/**
 * Interface requise pour le compteur
 * @author Willy Nassim
 *
 */
public interface IConsommation extends RequiredI {
	
	/**
	 * Retourne la consommation electrique d'un appareil
	 * @return
	 * @throws Exception
	 */
	public double envoyerConsommation(String uri) throws Exception;
}
