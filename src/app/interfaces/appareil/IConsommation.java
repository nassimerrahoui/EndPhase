package app.interfaces.appareil;

import fr.sorbonne_u.components.interfaces.OfferedI;

/**
 * Permet aux appareils d'offrir une interface pour le compteur
 * @author Willy Nassim
 *
 */
public interface IConsommation extends OfferedI {
	
	/**
	 * Retourne la consommation electrique d'un appareil
	 * @return
	 * @throws Exception
	 */
	public double envoyerConsommation() throws Exception;
}
