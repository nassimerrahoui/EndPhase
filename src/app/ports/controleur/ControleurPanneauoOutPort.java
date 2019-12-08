package app.ports.controleur;

import app.interfaces.controleur.IControlePanneau;
import app.util.EtatUniteProduction;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class ControleurPanneauoOutPort extends AbstractOutboundPort implements IControlePanneau {

	private static final long serialVersionUID = 1L;

	public ControleurPanneauoOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, IControlePanneau.class, owner);
	}

	public ControleurPanneauoOutPort(ComponentI owner) throws Exception {
		super(IControlePanneau.class, owner);
	}

	@Override
	public void envoyerEtatUniteProduction(EtatUniteProduction etat) throws Exception {
		((IControlePanneau) this.connector).envoyerEtatUniteProduction(etat);
	}

}
