package app.ports.controleur;

import app.interfaces.controleur.IControleLaveLinge;
import app.util.EtatAppareil;
import app.util.ModeLaveLinge;
import app.util.TemperatureLaveLinge;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class ControleurLaveLingeOutPort extends AbstractOutboundPort implements IControleLaveLinge {

	private static final long serialVersionUID = 1L;

	public ControleurLaveLingeOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, IControleLaveLinge.class, owner);
	}
	
	public ControleurLaveLingeOutPort(ComponentI owner) throws Exception {
		super(IControleLaveLinge.class, owner);
	}

	@Override
	public void envoyerEtatAppareil(EtatAppareil etat) throws Exception {
		((IControleLaveLinge)this.connector).envoyerEtatAppareil(etat);
	}
	
	@Override
	public void envoyerPlanificationCycle(int heure, int minutes) throws Exception {
		((IControleLaveLinge)this.connector).envoyerPlanificationCycle(heure, minutes);
	}

	@Override
	public void envoyerPlanificationMode(ModeLaveLinge ml, int heure, int minutes) throws Exception {
		((IControleLaveLinge)this.connector).envoyerPlanificationMode(ml, heure, minutes);
	}

	@Override
	public void envoyerTemperature(TemperatureLaveLinge tl) throws Exception {
		((IControleLaveLinge)this.connector).envoyerTemperature(tl);
	}

}
