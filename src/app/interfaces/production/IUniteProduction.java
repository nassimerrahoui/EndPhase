package app.interfaces.production;

import app.util.EtatUniteProduction;
import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

/**
 * Offre la possibilite au controleur de definir un etat
 * Requiert un ajout a la liste des unites de production du controleur
 * @author Willy Nassim
 *
 */
public interface IUniteProduction extends OfferedI, RequiredI {

	/**
	 * Permet d'eteindre ou allumer une unite de production
	 * @param etat
	 * @throws Exception
	 */
	public void setEtatUProduction(EtatUniteProduction etat) throws Exception;
	
	/**
	 * Permet de demander au controleur d'integrer le systeme
	 * @param uri
	 * @throws Exception
	 */
	public void demandeAjoutControleur(String uri) throws Exception;
}
