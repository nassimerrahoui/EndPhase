package app.interfaces.appareil;

import app.util.ModeOrdinateur;
import fr.sorbonne_u.components.interfaces.OfferedI;

/**
 * Permet a l'ordinateur de proposer une interface de controle pour leur controleur
 * @author Willy Nassim
 *
 */
public interface IOrdinateur extends OfferedI {

	/**
	 * Permet de changer l'etat de l'ordinateur
	 * @param etat
	 * @throws Exception
	 */
	public void setModeOrdinateur(ModeOrdinateur etat) throws Exception;
}
