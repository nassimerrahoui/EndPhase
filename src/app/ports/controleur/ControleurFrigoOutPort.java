package app.ports.controleur;

import app.interfaces.appareil.IFrigo;
import app.util.EtatAppareil;
import app.util.ModeFrigo;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class ControleurFrigoOutPort extends AbstractOutboundPort implements IFrigo {

	private static final long serialVersionUID = 1L;

	public ControleurFrigoOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, IFrigo.class, owner);
	}
	
	public ControleurFrigoOutPort(ComponentI owner) throws Exception {
		super(IFrigo.class, owner);
	}

	@Override
	public void setEtatAppareil(EtatAppareil etat) throws Exception {
		((ControleurFrigoOutPort)this.connector).setEtatAppareil(etat);
	}

	@Override
	public void setTemperature_Refrigerateur(double temperature) throws Exception {
		((ControleurFrigoOutPort)this.connector).setTemperature_Refrigerateur(temperature);
	}

	@Override
	public void setTemperature_Congelateur(double temperature) throws Exception {
		((ControleurFrigoOutPort)this.connector).setTemperature_Congelateur(temperature);
	}

	@Override
	public void setLumiere_Refrigerateur(ModeFrigo mf) throws Exception {
		((ControleurFrigoOutPort)this.connector).setLumiere_Refrigerateur(mf);
	}

	@Override
	public void setLumiere_Congelateur(ModeFrigo mf) throws Exception {
		((ControleurFrigoOutPort)this.connector).setLumiere_Congelateur(mf);
	}


}
