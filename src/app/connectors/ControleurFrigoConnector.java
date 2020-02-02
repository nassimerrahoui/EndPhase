package app.connectors;

import app.interfaces.appareil.IFrigo;
import app.interfaces.controleur.IControleFrigo;
import app.util.ModeFrigo;
import fr.sorbonne_u.components.connectors.AbstractConnector;

/**
 * @author Willy Nassim
 */

public class ControleurFrigoConnector extends AbstractConnector implements IControleFrigo {

	@Override
	public void envoyerModeFrigo(ModeFrigo etat) throws Exception {
		((IFrigo) this.offering).setModeFrigo(etat);
	}

	@Override
	public void envoyerTemperature_Refrigerateur(double temperature) throws Exception {
		((IFrigo) this.offering).setTemperature_Refrigerateur(temperature);
	}

	@Override
	public void envoyerTemperature_Congelateur(double temperature) throws Exception {
		((IFrigo) this.offering).setTemperature_Congelateur(temperature);
	}
}
