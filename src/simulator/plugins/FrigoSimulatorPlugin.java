package simulator.plugins;

import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import simulator.models.frigo.FrigoModel;

public class FrigoSimulatorPlugin extends AtomicSimulatorPlugin {
	private static final long serialVersionUID = 1L;
	
	@Override
	public Object getModelStateValue(String modelURI, String name) throws Exception {

		ModelDescriptionI m = this.simulator.getDescendentModel(modelURI);

		assert m instanceof FrigoModel;

		if (name.equals("state")) {
			return ((FrigoModel) m).getState();
		} else if(name.equals("consommation")){
			assert name.equals("consommation");
			return ((FrigoModel) m).getConsommation();
		} else {
			assert name.equals("temperature");
			return ((FrigoModel) m).getCurrentTemperature();
		}
	}
}
