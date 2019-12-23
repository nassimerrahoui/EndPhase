package app.ports.controleur;

import app.interfaces.controleur.IControleFrigo;
import app.util.ModeFrigo;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class ControleurFrigoOutPort extends AbstractOutboundPort implements IControleFrigo {

	private static final long serialVersionUID = 1L;

	public ControleurFrigoOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, IControleFrigo.class, owner);
	}
	
	public ControleurFrigoOutPort(ComponentI owner) throws Exception {
		super(IControleFrigo.class, owner);
	}
	
	@Override
	public void envoyerModeFrigo(ModeFrigo etat) throws Exception {
		((IControleFrigo)this.connector).envoyerModeFrigo(etat);
	}

	@Override
	public void envoyerTemperature_Refrigerateur(double temperature) throws Exception {
		((IControleFrigo)this.connector).envoyerTemperature_Refrigerateur(temperature);
	}

	@Override
	public void envoyerTemperature_Congelateur(double temperature) throws Exception {
		((IControleFrigo)this.connector).envoyerTemperature_Congelateur(temperature);
	}
}
