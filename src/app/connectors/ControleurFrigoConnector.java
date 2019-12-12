package app.connectors;

import app.interfaces.appareil.IFrigo;
import app.interfaces.controleur.IControleFrigo;
import app.util.EtatAppareil;
import app.util.ModeFrigo;
import fr.sorbonne_u.components.connectors.AbstractConnector;

public class ControleurFrigoConnector extends AbstractConnector implements IControleFrigo {

	@Override
	public void envoyerEtatAppareil(EtatAppareil etat) throws Exception {
		((IFrigo) this.offering).setEtatAppareil(etat);
	}

	@Override
	public void envoyerTemperature_Refrigerateur(double temperature) throws Exception {
		((IFrigo) this.offering).setTemperature_Refrigerateur(temperature);
	}

	@Override
	public void envoyerTemperature_Congelateur(double temperature) throws Exception {
		((IFrigo) this.offering).setTemperature_Congelateur(temperature);
	}

	@Override
	public void envoyerLumiere_Refrigerateur(ModeFrigo mf) throws Exception {
		((IFrigo) this.offering).setLumiere_Refrigerateur(mf);
	}

	@Override
	public void envoyerLumiere_Congelateur(ModeFrigo mf) throws Exception {
		((IFrigo) this.offering).setLumiere_Congelateur(mf);
	}
}
