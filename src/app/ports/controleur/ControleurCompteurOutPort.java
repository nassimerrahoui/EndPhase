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
	public void demanderAjoutAppareil(String uri) throws Exception {
		((IControleCompteur) this.connector).demanderAjoutAppareil(uri);
	}
	
	@Override
	public void demanderAjoutUniteProduction(String uri) throws Exception {
		((IControleCompteur) this.connector).demanderAjoutUniteProduction(uri);
	}

	@Override
	public double getConsommationGlobale() throws Exception {
		return ((IControleCompteur) this.connector).getConsommationGlobale();
	}

	@Override
	public double getProductionGlobale() throws Exception {
		return ((IControleCompteur) this.connector).getProductionGlobale();
	}
}
