package simulator.plugins;

import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import simulator.models.aspirateur.AspirateurModel;

public class AspirateurSimulatorPlugin extends AtomicSimulatorPlugin {
	private static final long serialVersionUID = 1L;

	@Override
	public Object getModelStateValue(String modelURI, String name) throws Exception {

		ModelDescriptionI m = this.simulator.getDescendentModel(modelURI);

		assert m instanceof AspirateurModel;

		if (name.equals("state")) {
			return ((AspirateurModel) m).getState();
		} else {
			assert name.equals("consommation");
			return ((AspirateurModel) m).getConsommation();
		}
	}
}
