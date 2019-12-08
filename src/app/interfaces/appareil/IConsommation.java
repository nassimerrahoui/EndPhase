package app.interfaces.appareil;

import fr.sorbonne_u.components.interfaces.RequiredI;

/**
 * Interface requise par les appareils pour le compteur
 * @author Willy Nassim
 *
 */
public interface IConsommation extends RequiredI {
	
	/**
	 * Envoi la consommation electrique d'un appareil au compteur
	 * @return
	 * @throws Exception
	 */
	public void envoyerConsommation(String uri, double consommation) throws Exception;
}
