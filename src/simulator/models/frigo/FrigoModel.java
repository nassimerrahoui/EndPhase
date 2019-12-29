package simulator.models.frigo;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import app.util.ModeFrigo;
import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentStateAccessI;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOAwithEquations;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.AbstractSimulationReport;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.utils.PlotterDescription;
import fr.sorbonne_u.utils.XYPlotter;
import simulator.events.frigo.AbstractFrigoEvent;
import simulator.events.frigo.CloseRefrigerateurDoor;
import simulator.events.frigo.OpenRefrigerateurDoor;
import simulator.events.frigo.SwitchFrigoOff;
import simulator.events.frigo.SwitchFrigoOn;

@ModelExternalEvents(imported = { 
		SwitchFrigoOn.class,
		SwitchFrigoOff.class,
		OpenRefrigerateurDoor.class,
		CloseRefrigerateurDoor.class,
})

public class FrigoModel extends AtomicHIOAwithEquations {

	public static class FrigoReport extends AbstractSimulationReport {
		private static final long serialVersionUID = 1L;

		public FrigoReport(String modelURI) {
			super(modelURI);
		}

		@Override
		public String toString() {
			return "FrigoReport(" + this.getModelURI() + ")";
		}
	}
	
	private static final long serialVersionUID = 1L;
	public static final String URI = "FrigoModel";
	public static final String COMPONENT_REF = "frigo-component-ref";
	public static final String POWER_PLOTTING_PARAM_NAME = "consommation";
	public static final String TEMPERATURE_PLOTTING_PARAM_NAME = "temperature";
	public static final String STATE_PLOTTING_PARAM_NAME = "state";
	
	private static final String SERIES_POWER = "frigo_power";
	private static final String SERIES_TEMPERATURE = "frigo_temperature";
	private static final String SERIES_MODE= "frigo_state";
	
	// Temperature initiale du refrigerateur eteint 
	public static final double AMBIENT_TEMPERATURE = 20.0; // degres celsius
	protected static final double CONSOMMAION_INITIALE = 300; // Watts
	protected static final double CONSOMMATION_LUMIERE_ALLUMEE = 30; // Watts
	
	protected double currentPower; // Watts
	protected double currentTemperature; // degres celsius
	protected ModeFrigo currentState;
	
	protected XYPlotter powerPlotter;
	protected XYPlotter temperaturePlotter;
	protected XYPlotter statePlotter;
	
	protected EmbeddingComponentStateAccessI componentRef;
	
	public FrigoModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		this.setLogger(new StandardLogger());
	}

	@Override
	public void setSimulationRunParameters(Map<String, Object> simParams) throws Exception {
		
		this.componentRef = (EmbeddingComponentStateAccessI) simParams.get(URI + " : " + COMPONENT_REF);

		PlotterDescription pd = (PlotterDescription) simParams.get(URI + " : " + POWER_PLOTTING_PARAM_NAME) ;
		this.powerPlotter = new XYPlotter(pd);
		this.powerPlotter.createSeries(SERIES_POWER);
		
		pd = (PlotterDescription) simParams.get(URI + " : " + STATE_PLOTTING_PARAM_NAME) ;
		this.statePlotter = new XYPlotter(pd);
		this.statePlotter.createSeries(SERIES_MODE);
		
		pd = (PlotterDescription) simParams.get(URI + " : " + TEMPERATURE_PLOTTING_PARAM_NAME) ;
		this.temperaturePlotter = new XYPlotter(pd);
		this.temperaturePlotter.createSeries(SERIES_TEMPERATURE);
	}

	@Override
	public void initialiseState(Time initialTime) {
		
		this.currentPower = CONSOMMAION_INITIALE;
		this.currentState = ModeFrigo.LIGHT_OFF;	
		this.currentTemperature = AMBIENT_TEMPERATURE;
		
		if(this.powerPlotter != null) {
			this.powerPlotter.initialise();
			this.powerPlotter.showPlotter();
		}
		
		if(this.temperaturePlotter != null) {
			this.temperaturePlotter.initialise();
			this.temperaturePlotter.showPlotter();
		}
		
		if(this.statePlotter != null) {
			this.statePlotter.initialise();
			this.statePlotter.showPlotter();
		}
		
		try {
			//this.setDebugLevel(1);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		super.initialiseState(initialTime);
		
		this.powerPlotter.addData(SERIES_POWER, this.getCurrentStateTime().getSimulatedTime(), this.getConsommation());
		this.statePlotter.addData(SERIES_MODE, this.getCurrentStateTime().getSimulatedTime(), this.currentState.getMode());
		this.temperaturePlotter.addData(SERIES_TEMPERATURE, initialTime.getSimulatedTime(), AMBIENT_TEMPERATURE);
	}

	@Override
	public Vector<EventI> output() {
		// the model does not export any event.
		return null;
	}

	@Override
	public Duration timeAdvance() {
		if (this.componentRef == null) {
			return Duration.INFINITY;
		} else {
			return new Duration(10.0, TimeUnit.SECONDS);
		}
	}
	
	@Override
	public void userDefinedInternalTransition(Duration elapsedTime) {
		
		if (this.componentRef != null) {
			try {
				this.logMessage("frigo state = " + componentRef.getEmbeddingComponentStateValue(URI + " : state"));
				this.logMessage("frigo consommation = " + componentRef.getEmbeddingComponentStateValue(URI + " : consommation"));
				this.logMessage("frigo temperature = " + componentRef.getEmbeddingComponentStateValue(URI + " : refrigerateur_temperature"));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		super.userDefinedInternalTransition(elapsedTime) ;

		double delta_t = elapsedTime.getSimulatedDuration() ;
		this.computeNewLevel(this.getCurrentStateTime(), delta_t) ;
		
	}
	
	public double getConsommationFromTemperature(double temperature, double old_temperature) {
		if(temperature < old_temperature)
			return currentPower + currentPower * 0.005;
		else if(temperature > old_temperature)
			return currentPower - currentPower * 0.005;
		else if(temperature == old_temperature)
			return currentPower;
		else
			return 0;
	}
	
	protected void computeNewLevel(Time current, double delta_t) {
		double old_temperature = currentTemperature;
		double variation_temperature = 0.04;
		
		if(currentState == ModeFrigo.OFF) {
			if(currentTemperature < AMBIENT_TEMPERATURE && delta_t >= 1.0) {
				currentTemperature += variation_temperature;
				delta_t--;
			}
			currentPower = getConsommationFromTemperature(currentTemperature, old_temperature);
			return;
		}
		
		try {
			double temperature_cible = (double) componentRef.getEmbeddingComponentStateValue(URI + " : refrigerateur_temperature_cible");
			
			if(temperature_cible < currentTemperature) {
				while(delta_t >= 1.0) {
					if(temperature_cible > currentTemperature - variation_temperature)
						break;
					currentTemperature -= variation_temperature;
					delta_t--;
				}
			} else if(temperature_cible > currentTemperature) {
				while(delta_t >= 1.0) {
					if(temperature_cible < currentTemperature + variation_temperature)
						break;
					currentTemperature += variation_temperature;
					delta_t--;
				}
			}
			
			currentPower = getConsommationFromTemperature(currentTemperature, old_temperature);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void userDefinedExternalTransition(Duration elapsedTime) {
		
		if (this.hasDebugLevel(2)) {
			this.logMessage("FrigoModel::userDefinedExternalTransition 1");
		}
		Vector<EventI> currentEvents = this.getStoredEventAndReset();
		assert currentEvents != null && currentEvents.size() == 1;
		Event ce = (Event) currentEvents.get(0);
		assert ce instanceof AbstractFrigoEvent;
		if (this.hasDebugLevel(2)) {
			this.logMessage("FrigorModel::userDefinedExternalTransition 2 " + ce.getClass().getCanonicalName());
		}

		this.powerPlotter.addData(SERIES_POWER, this.getCurrentStateTime().getSimulatedTime(), this.getConsommation());
		this.statePlotter.addData(SERIES_MODE, this.getCurrentStateTime().getSimulatedTime(), this.getState().getMode());
		this.temperaturePlotter.addData(SERIES_TEMPERATURE, this.getCurrentStateTime().getSimulatedTime(), this.getCurrentTemperature());

		if (this.hasDebugLevel(2)) {
			this.logMessage("FrigoModel::userDefinedExternalTransition 3 " + this.getState());
		}
		ce.executeOn(this);
		if (this.hasDebugLevel(1)) {
			this.logMessage("FrigoModel::userDefinedExternalTransition 4 " + this.getState());
		}

		this.powerPlotter.addData(SERIES_POWER, this.getCurrentStateTime().getSimulatedTime(), this.getConsommation());
		this.statePlotter.addData(SERIES_MODE, this.getCurrentStateTime().getSimulatedTime(), this.getState().getMode());
		this.temperaturePlotter.addData(SERIES_TEMPERATURE, this.getCurrentStateTime().getSimulatedTime(), this.getCurrentTemperature());

		super.userDefinedExternalTransition(elapsedTime);
		if (this.hasDebugLevel(2)) {
			this.logMessage("FrigoModel::userDefinedExternalTransition 5");
		}
	}
	
	@Override
	public void endSimulation(Time endTime) throws Exception {
		this.powerPlotter.addData(SERIES_POWER, endTime.getSimulatedTime(), this.getConsommation());
		this.statePlotter.addData(SERIES_MODE, endTime.getSimulatedTime(), this.getState().getMode());
		this.temperaturePlotter.addData(SERIES_TEMPERATURE, endTime.getSimulatedTime(), this.getCurrentTemperature());
		Thread.sleep(10000L);
		this.powerPlotter.dispose();
		this.statePlotter.dispose();
		this.temperaturePlotter.dispose();

		super.endSimulation(endTime);
	}

	@Override
	public SimulationReportI getFinalReport() throws Exception {
		return new FrigoReport(this.getURI());
	}

	public void setState(ModeFrigo s) {
		if(currentState != ModeFrigo.LIGHT_ON && s == ModeFrigo.LIGHT_ON) {
			currentPower += CONSOMMATION_LUMIERE_ALLUMEE;
		} else if (currentState == ModeFrigo.LIGHT_ON && s != ModeFrigo.LIGHT_ON) {
			currentPower -= CONSOMMATION_LUMIERE_ALLUMEE;
		}
		this.currentState = s;
	}

	public ModeFrigo getState() {
		return this.currentState;
	}

	public double getConsommation() {
		return this.currentPower;
	}
	
	public double getCurrentTemperature() {
		return this.currentTemperature;
	}
}
