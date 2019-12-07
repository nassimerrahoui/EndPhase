package app.ports.panneausolaire;

import app.interfaces.production.IAjoutUniteProduction;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class PanneauControleurOutPort extends AbstractOutboundPort implements IAjoutUniteProduction{

	private static final long serialVersionUID = 1L;

	public PanneauControleurOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, IAjoutUniteProduction.class, owner);
	}
	
	public PanneauControleurOutPort(ComponentI owner) throws Exception {
		super(IAjoutUniteProduction.class, owner);
	}

	@Override
	public void demandeAjoutControleur(String uri) throws Exception {
		((IAjoutUniteProduction) this.connector).demandeAjoutControleur(uri);
		
	}

}
