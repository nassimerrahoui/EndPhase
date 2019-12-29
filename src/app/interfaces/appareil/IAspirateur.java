package app.interfaces.appareil;

import app.util.ModeAspirateur;
import fr.sorbonne_u.components.interfaces.OfferedI;

/**
 * Permet a l'aspirateur de proposer une interface de controle pour leur controleur
 * @author Willy Nassim
 *
 */
public interface IAspirateur extends OfferedI {

	/**
	 * Permet de changer l'etat de l'aspirateur
	 * @param etat
	 * @throws Exception
	 */
	public void setModeAspirateur(ModeAspirateur etat) throws Exception;
}
