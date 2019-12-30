package app.ports.controleur;

import java.util.ArrayList;

import app.interfaces.controleur.IControleLaveLinge;
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
	public void envoyerModeLaveLinge(ModeLaveLinge etat) throws Exception {
		((IControleLaveLinge)this.connector).envoyerModeLaveLinge(etat);
	}
	
	@Override
	public void envoyerPlanificationCycle(ArrayList<ModeLaveLinge> planification, int heure, int minutes) throws Exception {
		((IControleLaveLinge)this.connector).envoyerPlanificationCycle(planification, heure, minutes);
	}

	@Override
	public void envoyerTemperature(TemperatureLaveLinge tl) throws Exception {
		((IControleLaveLinge)this.connector).envoyerTemperature(tl);
	}
}
