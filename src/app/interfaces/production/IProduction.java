package app.interfaces.production;

import fr.sorbonne_u.components.interfaces.OfferedI;

/**
 * Permet aux unites de production d'offrir une interface pour le compteur 
 * @author Willy Nassim
 *
 */
public interface IProduction extends OfferedI {
	
	/**
	 * Retourne la production electrique d'une unite de production
	 * @return
	 * @throws Exception
	 */
	public double envoyerProduction() throws Exception;
}
