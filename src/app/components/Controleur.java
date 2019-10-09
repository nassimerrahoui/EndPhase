package app.components;

import app.interfaces.IAppareil;
import app.interfaces.IControleur;
import app.interfaces.IFrigo;
import app.interfaces.IOrdinateur;
import app.interfaces.IChargeur;
import fr.sorbonne_u.components.AbstractComponent;

public class Controleur extends AbstractComponent implements IControleur {

	protected Controleur(int nbThreads, int nbSchedulableThreads) {
		super(nbThreads, nbSchedulableThreads);
		
	}

	@Override
	public void action(IAppareil appareil) {
		
		if(appareil instanceof IFrigo) {
			/****/
		}
		
		if(appareil instanceof IOrdinateur) {
			
		}
		
		if(appareil instanceof IChargeur) {
			
		}
	}

}
