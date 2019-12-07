package app.interfaces.appareil;

import app.util.EtatAppareil;
import app.util.ModeFrigo;
import fr.sorbonne_u.components.interfaces.OfferedI;

/**
 * Definit une interface offerte pour donner le controle au controleur
 * @author Willy Nassim
 *
 */
public interface IFrigo extends OfferedI  {
	
	/**
	 * Permet d'eteindre ou allumer un appareil
	 * @param etat
	 * @throws Exception
	 */
	public void setEtatAppareil(EtatAppareil etat) throws Exception;
	
	/**
	 * Permet d'etablir la temperature a atteindre pour le refrigerateur 
	 * @param temperature
	 */
	public void setTemperature_Refrigerateur(double temperature) throws Exception;
	
	/**
	 * Permet d'etablir la temperature a atteindre pour le congelateur
	 * @param temperature
	 */
	public void setTemperature_Congelateur(double temperature) throws Exception;
	
	/**
	 * Permet d'allumer ou eteindre la lumiere du refrigerateur
	 * @param mf
	 */
	public void setLumiere_Refrigerateur(ModeFrigo mf) throws Exception;

	/**
	 * Permet d'allumer ou eteindre la lumiere du congelateur
	 * @param mf
	 */
	public void setLumiere_Congelateur(ModeFrigo mf) throws Exception;
}
