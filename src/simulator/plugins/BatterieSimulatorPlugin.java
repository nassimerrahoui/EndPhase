package simulator.plugins;
import java.util.Map;

import app.components.Batterie;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import fr.sorbonne_u.utils.PlotterDescription;
import simulator.models.batterie.BatterieModel;

/**
 * @author Willy Nassim
 */

public class BatterieSimulatorPlugin extends AtomicSimulatorPlugin {
	private static final long serialVersionUID = 1L;
	
	@Override
	public void setSimulationRunParameters(Map<String, Object> simParams) throws Exception {
		simParams.put(BatterieModel.URI + " : " + BatterieModel.COMPONENT_REF, this.owner);
		
		simParams.put(BatterieModel.URI + " : " + BatterieModel.PRODUCTION_PLOTTING_PARAM_NAME, new PlotterDescription(
				"Production Batterie", 
				"Temps (sec)", 
				"Production (Watt)", 
				Batterie.ORIGIN_X + Batterie.getPlotterWidth(),
				Batterie.ORIGIN_Y + 2 * Batterie.getPlotterHeight(),
				Batterie.getPlotterWidth(),
				Batterie.getPlotterHeight())) ;
		
		
		super.setSimulationRunParameters(simParams);
	}
	
	
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
