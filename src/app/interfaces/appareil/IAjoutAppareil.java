package app.interfaces.appareil;

import fr.sorbonne_u.components.interfaces.RequiredI;

/**
 * Defini l'interface requis pour l'ajout a la liste des appareils du controleur
 * @author Willy Nassim
 *
 */
public interface IAjoutAppareil extends RequiredI {
	
	/**
	 * Permet de demander au controleur d'integrer le systeme
	 * @param uri
	 * @throws Exception
	 */
	public void demandeAjoutControleur(String uri) throws Exception;
}
