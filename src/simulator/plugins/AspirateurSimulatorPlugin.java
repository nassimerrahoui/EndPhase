package simulator.plugins;

import java.util.Map;

import app.components.Aspirateur;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import fr.sorbonne_u.utils.PlotterDescription;
import simulator.models.aspirateur.AspirateurModel;

/**
 * @author Willy Nassim
 */

public class AspirateurSimulatorPlugin extends AtomicSimulatorPlugin {
	private static final long serialVersionUID = 1L;
	
	@Override
	public void setSimulationRunParameters(Map<String, Object> simParams) throws Exception {
		simParams.put(AspirateurModel.URI + " : " + AspirateurModel.COMPONENT_REF, this.owner);
		
		simParams.put(AspirateurModel.URI + " : " + AspirateurModel.POWER_PLOTTING_PARAM_NAME, new PlotterDescription(
				"Consommation Aspirateur", 
				"Temps (sec)", 
				"Consommation (Watt)", 
				Aspirateur.ORIGIN_X - Aspirateur.getPlotterWidth(),
		  		Aspirateur.ORIGIN_Y,
		  		Aspirateur.getPlotterWidth(),
		  		Aspirateur.getPlotterHeight())) ;
		
		super.setSimulationRunParameters(simParams);
	}
	
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
