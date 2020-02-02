package simulator.plugins;

import java.util.Map;

import app.components.LaveLinge;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import fr.sorbonne_u.utils.PlotterDescription;
import simulator.models.lavelinge.LaveLingeModel;

/**
 * @author Willy Nassim
 */

public class LaveLingeSimulatorPlugin extends AtomicSimulatorPlugin {
	private static final long serialVersionUID = 1L;
	
	@Override
	public void setSimulationRunParameters(Map<String, Object> simParams) throws Exception {
		simParams.put(LaveLingeModel.URI + " : " + LaveLingeModel.COMPONENT_REF, this.owner);
		
		simParams.put(LaveLingeModel.URI + " : " + LaveLingeModel.POWER_PLOTTING_PARAM_NAME, new PlotterDescription(
				"Consommation Lave-Linge", 
				"Temps (sec)", 
				"Consommation (Watt)", 
				LaveLinge.ORIGIN_X ,
				LaveLinge.ORIGIN_Y,
				LaveLinge.getPlotterWidth(),
				LaveLinge.getPlotterHeight())) ;
		
		super.setSimulationRunParameters(simParams);
	}

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
