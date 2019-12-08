package app.interfaces.production;

import app.util.EtatUniteProduction;
import fr.sorbonne_u.components.interfaces.OfferedI;

/**
 *  Definit une interface offerte pour donner le controle au controleur
 * @author Willy Nassim
 *
 */
public interface IBatterie extends OfferedI{
	
	/**
	 * Permet d'eteindre ou allumer une unite de production
	 * @param etat
	 * @throws Exception
	 */
	public void setEtatUniteProduction(EtatUniteProduction etat) throws Exception;
}
