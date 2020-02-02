package simulator.plugins;

import java.util.Map;

import app.components.Frigo;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import fr.sorbonne_u.utils.PlotterDescription;
import simulator.models.frigo.FrigoModel;

/**
 * @author Willy Nassim
 */

public class FrigoSimulatorPlugin extends AtomicSimulatorPlugin {
	private static final long serialVersionUID = 1L;
	
	@Override
	public void setSimulationRunParameters(Map<String, Object> simParams) throws Exception {
		simParams.put(FrigoModel.URI + " : " + FrigoModel.COMPONENT_REF, this.owner);
		simParams.put(FrigoModel.URI + " : " + FrigoModel.POWER_PLOTTING_PARAM_NAME, new PlotterDescription(
				"Frigo Model - Consommation",
				"Time (sec)",
				"Consommation (W)",
				Frigo.ORIGIN_X + Frigo.getPlotterWidth(),
				Frigo.ORIGIN_Y,
				Frigo.getPlotterWidth(),
				Frigo.getPlotterHeight())) ;
		simParams.put(FrigoModel.URI + " : " + FrigoModel.STATE_PLOTTING_PARAM_NAME, new PlotterDescription(
				"Frigo Model - Etat",
				"Time (sec)",
				"Etat",
				Frigo.ORIGIN_X + 2 * Frigo.getPlotterWidth(),
				Frigo.ORIGIN_Y,
				Frigo.getPlotterWidth(),
				Frigo.getPlotterHeight())) ;
		simParams.put(FrigoModel.URI + " : " + FrigoModel.TEMPERATURE_PLOTTING_PARAM_NAME, new PlotterDescription(
				"Frigo Model - Temperature",
				"Time (sec)",
				"Temperature (Degres Celcius)",
				Frigo.ORIGIN_X + 2 * Frigo.getPlotterWidth(),
				Frigo.ORIGIN_Y + Frigo.getPlotterHeight(),
				Frigo.getPlotterWidth(),
				Frigo.getPlotterHeight())) ;
		super.setSimulationRunParameters(simParams);
	}
	
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
