package app.ports.controleur;

import app.interfaces.controleur.IControleOrdinateur;
import app.util.EtatAppareil;
import app.util.ModeOrdinateur;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class ControleurOrdinateurOutPort extends AbstractOutboundPort implements IControleOrdinateur {

	private static final long serialVersionUID = 1L;

	public ControleurOrdinateurOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, IControleOrdinateur.class, owner);
	}
	
	public ControleurOrdinateurOutPort(ComponentI owner) throws Exception {
		super(IControleOrdinateur.class, owner);
	}
	
	@Override
	public void envoyerEtatAppareil(EtatAppareil etat) throws Exception {
		((IControleOrdinateur)this.connector).envoyerEtatAppareil(etat);
	}

	@Override
	public void envoyerMode(ModeOrdinateur mo) throws Exception {
		((IControleOrdinateur)this.connector).envoyerMode(mo);
	}
}
