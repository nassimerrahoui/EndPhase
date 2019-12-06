package app.ports.controleur;

import app.interfaces.controleur.ICompteur;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class ControleurCompteurOutPort extends AbstractOutboundPort implements ICompteur {

	private static final long serialVersionUID = 1L;

	public ControleurCompteurOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, ICompteur.class, owner);
	}
	
	public ControleurCompteurOutPort(ComponentI owner) throws Exception {
		super(ICompteur.class, owner);
	}

	@Override
	public void ajouterAppareil(String uri) throws Exception {
		((ControleurCompteurOutPort) this.connector).ajouterAppareil(uri);
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
