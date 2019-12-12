package app.ports.controleur;

import app.interfaces.controleur.IControlePanneau;
import app.util.EtatUniteProduction;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class ControleurPanneauOutPort extends AbstractOutboundPort implements IControlePanneau {

	private static final long serialVersionUID = 1L;

	public ControleurPanneauOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, IControlePanneau.class, owner);
	}

	public ControleurPanneauOutPort(ComponentI owner) throws Exception {
		super(IControlePanneau.class, owner);
	}

	@Override
	public void envoyerEtatUniteProduction(EtatUniteProduction etat) throws Exception {
		((IControlePanneau) this.connector).envoyerEtatUniteProduction(etat);
	}

}
