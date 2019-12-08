package app.ports.compteur;

import app.interfaces.compteur.ICompteur;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class CompteurOutPort extends AbstractOutboundPort implements ICompteur {

	private static final long serialVersionUID = 1L;

	public CompteurOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, ICompteur.class, owner);
	}
	
	public CompteurOutPort(ComponentI owner) throws Exception {
		super(ICompteur.class, owner);
	}

	@Override
	public double getAppareilConsommation() throws Exception {
		return ((ICompteur) this.connector).getAppareilConsommation();
	}

	@Override
	public double getUniteProduction() throws Exception {
		return ((ICompteur) this.connector).getUniteProduction();
	}
}
