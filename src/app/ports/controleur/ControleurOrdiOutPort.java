package app.ports.controleur;

import app.interfaces.appareil.IOrdinateur;
import app.util.EtatAppareil;
import app.util.ModeOrdinateur;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class ControleurOrdiOutPort extends AbstractOutboundPort implements IOrdinateur {

	private static final long serialVersionUID = 1L;

	public ControleurOrdiOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, IOrdinateur.class, owner);
	}
	
	public ControleurOrdiOutPort(ComponentI owner) throws Exception {
		super(IOrdinateur.class, owner);
	}

	@Override
	public void setEtatAppareil(EtatAppareil etat) throws Exception {
		((ControleurOrdiOutPort)this.connector).setEtatAppareil(etat);
	}

	@Override
	public void setMode(ModeOrdinateur mo) throws Exception {
		((ControleurOrdiOutPort)this.connector).setMode(mo);
	}

}
