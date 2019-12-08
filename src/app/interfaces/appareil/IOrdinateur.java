package app.interfaces.appareil;

import app.util.EtatAppareil;
import app.util.ModeOrdinateur;
import fr.sorbonne_u.components.interfaces.OfferedI;

/**
 * Permet a l'ordinateur de proposer une interface de controle pour leur controleur
 * @author Willy Nassim
 *
 */
public interface IOrdinateur extends OfferedI {

	/**
	 * Permet d'eteindre ou allumer un appareil
	 * @param etat
	 * @throws Exception
	 */
	public void setEtatAppareil(EtatAppareil etat) throws Exception;
	
	/**
	 * Permet de definir le mode de consommation electrique de l'ordinateur
	 * @param mo
	 * @throws Exception
	 */
	public void setMode(ModeOrdinateur mo) throws Exception;
}
