package app.ports.controleur;

import app.interfaces.controleur.IControleCompteur;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class ControleurCompteurOutPort extends AbstractOutboundPort implements IControleCompteur {

	private static final long serialVersionUID = 1L;

	public ControleurCompteurOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, IControleCompteur.class, owner);
	}

	public ControleurCompteurOutPort(ComponentI owner) throws Exception {
		super(IControleCompteur.class, owner);
	}

	@Override
	public double getAllConsommations() throws Exception {
		return ((ControleurCompteurOutPort) this.connector).getAllConsommations();
	}

	@Override
	public double getAllProductions() throws Exception {
		return ((ControleurCompteurOutPort) this.connector).getAllProductions();
	}
}
