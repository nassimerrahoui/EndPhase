package simulator.plugins;

import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import simulator.models.OrdinateurModel;

public class OrdinateurSimulatorPlugin extends AtomicSimulatorPlugin {
	private static final long serialVersionUID = 1L;

	@Override
	public Object getModelStateValue(String modelURI, String name) throws Exception {

		ModelDescriptionI m = this.simulator.getDescendentModel(modelURI);

		assert m instanceof OrdinateurModel;

		if (name.equals("state")) {
			return ((OrdinateurModel) m).getState();
		} else {
			assert name.equals("intensity");
			return ((OrdinateurModel) m).getIntensity();
		}
	}
}
