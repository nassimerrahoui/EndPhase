package simulator.plugins;

import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import simulator.models.lavelinge.LaveLingeModel;

public class LaveLingeSimulatorPlugin extends AtomicSimulatorPlugin{
	private static final long serialVersionUID = 1L;

	@Override
	public Object getModelStateValue(String modelURI, String name) throws Exception {

		ModelDescriptionI m = this.simulator.getDescendentModel(modelURI);

		assert m instanceof LaveLingeModel;

		if (name.equals("state")) {
			return ((LaveLingeModel) m).getState();
		} else {
			assert name.equals("consommation");
			return ((LaveLingeModel) m).getConsommation();
		}
	}
}
