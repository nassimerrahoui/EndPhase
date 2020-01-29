package simulator.plugins;

import java.util.Map;

import app.components.Compteur;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import fr.sorbonne_u.utils.PlotterDescription;
import simulator.models.compteur.CompteurModel;

public class CompteurSimulatorPlugin extends AtomicSimulatorPlugin {
	private static final long serialVersionUID = 1L;

	@Override
	public void setSimulationRunParameters(Map<String, Object> simParams) throws Exception {
		simParams.put(CompteurModel.URI + " : " + CompteurModel.COMPONENT_REF, this.owner);
		
		simParams.put(CompteurModel.URI + " : " + CompteurModel.CONSOMMATION_PLOTTING_PARAM_NAME, new PlotterDescription(
				"Compteur Model - Consommation Globale",
				"Time (sec)",
				"Consommation (W)",
				Compteur.ORIGIN_X + Compteur.getPlotterWidth(),
				Compteur.ORIGIN_Y + 5 * Compteur.getPlotterHeight(),
		  		Compteur.getPlotterWidth(),
		  		Compteur.getPlotterHeight())) ;
		simParams.put(CompteurModel.URI + " : " + CompteurModel.PRODUCTION_PLOTTING_PARAM_NAME, new PlotterDescription(
				"Compteur Model - Production globale",
				"Time (sec)",
				"Etat",
				Compteur.ORIGIN_X + 2 * Compteur.getPlotterWidth(),
				Compteur.ORIGIN_Y + 5 * Compteur.getPlotterHeight(),
		  		Compteur.getPlotterWidth(),
				Compteur.getPlotterHeight())) ;
		
		super.setSimulationRunParameters(simParams);
	}
	
	@Override
	public Object getModelStateValue(String modelURI, String name) throws Exception {
		ModelDescriptionI m = this.simulator.getDescendentModel(modelURI);

		assert m instanceof CompteurModel;

		if(name.equals("consommation")){
			assert name.equals("consommation");
			return ((CompteurModel) m).getConsommationGlobale();
		} else {
			assert name.equals("production");
			return ((CompteurModel) m).getProductionGlobale();
		}
	}
}