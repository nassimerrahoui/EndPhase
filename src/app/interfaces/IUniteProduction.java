package app.interfaces;

import app.util.EtatUniteProduction;
import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

public interface IUniteProduction extends OfferedI, RequiredI {

	/**
	 * Permet d'eteindre ou allumer une unite de production
	 * @param etat
	 * @throws Exception
	 */
	public void setEtatUProduction(EtatUniteProduction etat) throws Exception;
}
