package app.interfaces;

import app.util.EtatAppareil;
import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

public interface IAppareil extends OfferedI, RequiredI {

	/**
	 * Permet d'eteindre ou allumer un appareil
	 * @param etat
	 * @throws Exception
	 */
	public void setEtatAppareil(EtatAppareil etat) throws Exception;
	
	/**
	 * Permet de recuperer la consommation de l'appareil
	 * @return
	 * @throws Exception
	 */
	public double getConsomation() throws Exception;
	
}
