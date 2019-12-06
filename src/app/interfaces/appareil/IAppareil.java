package app.interfaces.appareil;

import app.util.EtatAppareil;
import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

/**
 * Offre la possibilite au controleur de definir un etat
 * Requiert un ajout a la liste des appareils du controleur
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
	
	/**
	 * Permet de demander au controleur d'integrer le systeme
	 * @param uri
	 * @throws Exception
	 */
	public void demandeAjoutControleur(String uri) throws Exception;
}
