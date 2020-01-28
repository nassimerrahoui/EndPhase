package simulator.plugins;

import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;

public class CompteurSimulatorPlugin extends AtomicSimulatorPlugin {
	private static final long serialVersionUID = 1L;
	public static final String CONSOMMATION_VARIABLE_NAME = "consommation";

	@Override
	public Object getModelStateValue(String modelURI, String name) throws Exception {
		@SuppressWarnings("unused")
		ModelDescriptionI m = this.simulator.getDescendentModel(modelURI);
		return null;
	}
}