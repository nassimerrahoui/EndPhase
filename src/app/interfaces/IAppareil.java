package app.interfaces;

import app.util.EtatAppareil;
import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

/**
 * Interface commune a tout les appareils
 * @author Willy Nassim
 *
 */
public interface IAppareil extends OfferedI, RequiredI {

	/**
	 * Permet d'eteindre ou allumer un appareil
	 * @param etat
	 * @throws Exception
	 */
	public void setEtatAppareil(EtatAppareil etat) throws Exception;
	

}
