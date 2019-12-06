package app.ports.controleur;

import app.interfaces.appareil.ILaveLinge;
import app.util.EtatAppareil;
import app.util.ModeLaveLinge;
import app.util.TemperatureLaveLinge;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class ControleurLaveLingeOutPort extends AbstractOutboundPort implements ILaveLinge {

	private static final long serialVersionUID = 1L;

	public ControleurLaveLingeOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, ILaveLinge.class, owner);
	}
	
	public ControleurLaveLingeOutPort(ComponentI owner) throws Exception {
		super(ILaveLinge.class, owner);
	}

	@Override
	public void setEtatAppareil(EtatAppareil etat) throws Exception {
		((ControleurLaveLingeOutPort)this.connector).setEtatAppareil(etat);
	}

	@Override
	public void planifierCycle(double heure) throws Exception {
		((ControleurLaveLingeOutPort)this.connector).planifierCycle(heure);
	}

	@Override
	public void planifierMode(ModeLaveLinge ml, double heure) throws Exception {
		((ControleurLaveLingeOutPort)this.connector).planifierMode(ml, heure);
	}

	@Override
	public void setTemperature(TemperatureLaveLinge tl) throws Exception {
		((ControleurLaveLingeOutPort)this.connector).setTemperature(tl);
	}

}
