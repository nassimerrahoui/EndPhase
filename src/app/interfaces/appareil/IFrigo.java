package app.interfaces.appareil;

import app.util.ModeFrigo;
import fr.sorbonne_u.components.interfaces.OfferedI;

/**
 * Definit une interface offerte pour donner le controle au controleur
 * @author Willy Nassim
 *
 */
public interface IFrigo extends OfferedI  {
	
	/**
	 * Permet de changer le mode du frigo
	 * @param etat
	 * @throws Exception
	 */
	public void setModeFrigo(ModeFrigo etat) throws Exception;
	
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

}
