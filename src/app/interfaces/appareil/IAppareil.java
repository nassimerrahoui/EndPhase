package app.interfaces.appareil;

import app.util.EtatAppareil;
import fr.sorbonne_u.components.interfaces.OfferedI;

/**
 * Offre la possibilite au controleur de definir un etat
 * @author Willy Nassim
 *
 */
public interface IAppareil extends OfferedI {

	/**
	 * Permet d'eteindre ou allumer un appareil
	 * @param etat
	 * @throws Exception
	 */
	public void setEtatAppareil(EtatAppareil etat) throws Exception;
}
