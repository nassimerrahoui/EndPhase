package app.ports.controleur;

import app.interfaces.controleur.IControleBatterie;
import app.util.EtatUniteProduction;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class ControleurBatterieOutPort extends AbstractOutboundPort implements IControleBatterie {

	private static final long serialVersionUID = 1L;

	public ControleurBatterieOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, IControleBatterie.class, owner);
	}

	public ControleurBatterieOutPort(ComponentI owner) throws Exception {
		super(IControleBatterie.class, owner);
	}

	@Override
	public void envoyerEtatUniteProduction(EtatUniteProduction etat) throws Exception {
		((ControleurBatterieOutPort) this.connector).envoyerEtatUniteProduction(etat);
	}
}
