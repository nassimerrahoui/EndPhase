package simulator.plugins;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import simulator.models.batterie.BatterieModel;

public class BatterieSimulatorPlugin extends AtomicSimulatorPlugin {
	private static final long serialVersionUID = 1L;
	
	
	
	
	@Override
	public Object getModelStateValue(String modelURI, String name) throws Exception {

		ModelDescriptionI m = this.simulator.getDescendentModel(modelURI);

		assert m instanceof BatterieModel;

		if (name.equals("state")) {
			return ((BatterieModel) m).getState();
		} else {
			assert name.equals("energy");
			return ((BatterieModel) m).getEnergy();
		}
	}
}
