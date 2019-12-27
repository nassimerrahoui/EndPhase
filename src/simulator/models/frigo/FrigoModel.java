package simulator.models.frigo;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import app.util.ModeFrigo;
import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentStateAccessI;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOAwithEquations;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
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
		CloseRefrigerateurDoor.class
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
	public static final double INITIAL_TEMPERATURE = 20.0; 
	protected static double CONSOMMATION_LUMIERE_ALLUMEE = 30; // Watts
	
	@ExportedVariable(type = Double.class)
	protected final Value<Double> currentPower = new Value<Double>(this, 0.0, 0); // Watts
	
	@ExportedVariable(type = Double.class)
	protected final Value<Double> currentTemperature = new Value<Double>(this, INITIAL_TEMPERATURE, 0); // Watts
	
	protected ModeFrigo currentState;
	
	protected XYPlotter powerPlotter;
	protected XYPlotter temperaturePlotter;
	protected XYPlotter statePlotter;
	
	protected EmbeddingComponentStateAccessI componentRef;
	
	public FrigoModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		
		// affichage de la consommation electrique sur le graphique
		super(uri, simulatedTimeUnit, simulationEngine);

		this.setLogger(new StandardLogger());
		
	}
	
	public double getConsommationFromTemperature(double temperature) {
		if(temperature < INITIAL_TEMPERATURE)
			return (INITIAL_TEMPERATURE - temperature) * 4;
		else
			return 0;
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
		this.currentState = ModeFrigo.LIGHT_OFF;	
		
		System.out.println(1);
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
			this.setDebugLevel(1);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		super.initialiseState(initialTime);
		
	}

	@Override
	protected void initialiseVariables(Time startTime) {
		this.currentPower.v = 0.0;
		
		this.powerPlotter.addData(SERIES_POWER, this.getCurrentStateTime().getSimulatedTime(), this.getConsommation());
		this.statePlotter.addData(SERIES_MODE, this.getCurrentStateTime().getSimulatedTime(), this.currentState.getMode());
		this.temperaturePlotter.addData(SERIES_TEMPERATURE, startTime.getSimulatedTime(), INITIAL_TEMPERATURE);
		
		super.initialiseVariables(startTime);
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
		
		System.out.println("OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO46541566");
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
	
	protected void	computeNewLevel(Time current, double delta_t) {
		// This method implements a linear progression of the power level,
		
		System.out.println("NEW LEVEL !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		
		double oldTemperature = this.getCurrentTemperature();
		ModeFrigo oldState = this.currentState;
		
		if(oldState == ModeFrigo.OFF)
			return;
		
		try {
			double temperature_cible = (double) componentRef.getEmbeddingComponentStateValue(URI + " : refrigerateur_temperature");
			double variation_temperature = 0.1;
			
			if(temperature_cible < oldTemperature) {
				while(delta_t >= 1.0) {
					if(temperature_cible > oldTemperature - variation_temperature)
						break;
					oldTemperature -= variation_temperature;
					delta_t--;
				}
			} else if(temperature_cible > oldTemperature) {
				while(delta_t >= 1.0) {
					if(temperature_cible < oldTemperature + variation_temperature)
						break;
					oldTemperature += variation_temperature;
					delta_t--;
				}
			}
				
			if(oldState == ModeFrigo.LIGHT_OFF) {
				this.currentPower.v = getConsommationFromTemperature(oldTemperature);
			}else if(oldState == ModeFrigo.LIGHT_ON) {
				this.currentPower.v = getConsommationFromTemperature(oldTemperature) + CONSOMMATION_LUMIERE_ALLUMEE;
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.currentTemperature.v = oldTemperature;
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
		this.powerPlotter.addData(SERIES_TEMPERATURE, this.getCurrentStateTime().getSimulatedTime(), this.getCurrentTemperature());

		if (this.hasDebugLevel(2)) {
			this.logMessage("FrigoModel::userDefinedExternalTransition 3 " + this.getState());
		}

		ce.executeOn(this);

		if (this.hasDebugLevel(1)) {
			this.logMessage("FrigoModel::userDefinedExternalTransition 4 " + this.getState());
		}

		this.powerPlotter.addData(SERIES_POWER, this.getCurrentStateTime().getSimulatedTime(), this.getConsommation());
		this.statePlotter.addData(SERIES_MODE, this.getCurrentStateTime().getSimulatedTime(), this.getState().getMode());
		this.powerPlotter.addData(SERIES_TEMPERATURE, this.getCurrentStateTime().getSimulatedTime(), this.getCurrentTemperature());

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
		this.currentState = s;
	}

	public ModeFrigo getState() {
		return this.currentState;
	}

	public double getConsommation() {
		return this.currentPower.v;
	}
	
	public double getCurrentTemperature() {
		return this.currentTemperature.v;
	}
}
