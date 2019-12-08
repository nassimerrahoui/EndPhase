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
	public double getFrigoConsommation() throws Exception {
		return ((ICompteur) this.connector).getFrigoConsommation();
	}

	@Override
	public double getLaveLingeConsommation() throws Exception {
		return ((ICompteur) this.connector).getLaveLingeConsommation();
	}

	@Override
	public double getOrdinateurConsommation() throws Exception {
		return ((ICompteur) this.connector).getOrdinateurConsommation();
	}

	@Override
	public double getPanneauProduction() throws Exception {
		return ((ICompteur) this.connector).getPanneauProduction();
	}

	@Override
	public double getBatterieProduction() throws Exception {
		return ((ICompteur) this.connector).getBatterieProduction();
	}
}
