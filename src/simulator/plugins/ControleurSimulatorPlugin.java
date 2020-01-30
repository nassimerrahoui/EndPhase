package simulator.plugins;

import java.util.Map;

import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import simulator.models.controleur.ControleurModel;

public class ControleurSimulatorPlugin extends AtomicSimulatorPlugin {
	private static final long serialVersionUID = 1L;
	
	@Override
	public void setSimulationRunParameters(Map<String, Object> simParams) throws Exception {
		simParams.put(ControleurModel.URI + " : " + ControleurModel.COMPONENT_REF, this);
		
		super.setSimulationRunParameters(simParams);
	}
	
	@Override
	public Object getModelStateValue(String modelURI, String name) throws Exception {
		ModelDescriptionI m = this.simulator.getDescendentModel(modelURI);

		assert m instanceof ControleurModel;

		return null;
	}
}