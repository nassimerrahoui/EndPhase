package simulator.plugins;

import java.util.Map;

import app.components.PanneauSolaire;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import fr.sorbonne_u.utils.PlotterDescription;
import simulator.models.panneausolaire.PanneauSolaireModel;

/**
 * @author Willy Nassim
 */

public class PanneauSolaireSimulatorPlugin extends AtomicSimulatorPlugin {
	private static final long serialVersionUID = 1L;

	@Override
	public void setSimulationRunParameters(Map<String, Object> simParams) throws Exception {

		simParams.put(PanneauSolaireModel.URI + " : " + PanneauSolaireModel.COMPONENT_REF, this.owner);
		
		simParams.put(PanneauSolaireModel.URI + " : " + PanneauSolaireModel.INTENSITY_PLOTTING_PARAM_NAME, new PlotterDescription(
				"Ensoleilement Panneau Solaire", 
				"Temps (sec)", 
				"Rayonnement (KWC)", 
				PanneauSolaire.ORIGIN_X + 2 * PanneauSolaire.getPlotterWidth(),
				PanneauSolaire.ORIGIN_Y + 2 * PanneauSolaire.getPlotterHeight(),
				PanneauSolaire.getPlotterWidth(),
				PanneauSolaire.getPlotterHeight())) ;
		
		super.setSimulationRunParameters(simParams);
	}
	
	@Override
	public Object getModelStateValue(String modelURI, String name) throws Exception {

		ModelDescriptionI m = this.simulator.getDescendentModel(modelURI);

		assert m instanceof PanneauSolaireModel;

		if (name.equals("state")) {
			return ((PanneauSolaireModel) m).getState();
		} else {
			assert name.equals("energy");
			return ((PanneauSolaireModel) m).getEnergy();
		}
	}
}
