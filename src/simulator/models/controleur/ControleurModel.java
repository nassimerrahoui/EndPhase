package simulator.models.controleur;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import app.components.Controleur;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

public class ControleurModel extends AtomicModel {


	private static final long serialVersionUID = 1L;
	
	public static final String URI = "ControleurModel";
	public static final String COMPONENT_REF = "controleur-component-ref";
	protected Controleur componentRef;

	public ControleurModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
	}

	
	@Override
	public void setSimulationRunParameters(Map<String, Object> simParams) throws Exception {
		this.componentRef = (Controleur) simParams.get(URI + " : " + COMPONENT_REF);
		System.out.println(this.componentRef);
	}

	
	@Override
	public Duration timeAdvance() {

		if (this.componentRef == null) {
			return Duration.INFINITY;
		} else {
			return new Duration(1.0, this.getSimulatedTimeUnit());
		}
	}

	@Override
	public ArrayList<EventI> output() {
		return null;
	}

	@Override
	public void userDefinedInternalTransition(Duration elapsedTime) {
		
		try {
			this.componentRef.controlTask(this.getCurrentStateTime().getSimulatedTime());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
}
