package app.ports.controleur;

import app.interfaces.controleur.IControleOrdinateur;
import app.util.EtatAppareil;
import app.util.ModeOrdinateur;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class ControleurOrdiOutPort extends AbstractOutboundPort implements IControleOrdinateur {

	private static final long serialVersionUID = 1L;

	public ControleurOrdiOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, IControleOrdinateur.class, owner);
	}
	
	public ControleurOrdiOutPort(ComponentI owner) throws Exception {
		super(IControleOrdinateur.class, owner);
	}
	
	@Override
	public void envoyerEtatAppareil(EtatAppareil etat) throws Exception {
		((ControleurOrdiOutPort)this.connector).envoyerEtatAppareil(etat);
	}

	@Override
	public void envoyerMode(ModeOrdinateur mo) throws Exception {
		((ControleurOrdiOutPort)this.connector).envoyerMode(mo);
	}
}
