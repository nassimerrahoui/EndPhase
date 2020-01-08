package simulator.models.frigo;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.random.RandomDataGenerator;

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
	
	protected XYPlotter powerPlotter;
	protected XYPlotter temperaturePlotter;
	protected XYPlotter statePlotter;
	
	/** Temperature initiale du refrigerateur eteint */
	public static final double AMBIENT_TEMPERATURE = 20.0; // Degres celsius
	protected static final double CONSOMMAION_REPOS = 10; // Watt
	protected static final double CONSOMMATION_INITIALE_COMPRESSEUR = 1200; // Watt
	protected static final double CONSOMMATION_EXECUTE_COMPRESSEUR = 70; // Watt
	
	/** Permet de generer des valeurs al�atoires */
	protected final RandomDataGenerator	rgNewVariationTemperature;
	
	/** Consommation actuelle du frigo */
	protected double currentPower; // Watt
	
	/** Temperature actuelle du frigo */
	protected double currentTemperature; // Degres celsius
	
	/** Etat actuel du frgio */
	protected ModeFrigo currentState;
	
	/** Vrai si le compresseur est allume */
	protected boolean compresseur;
	
	/** Difference maximale entre la temperature reele - la temperature cible 
	 * Si la limite est depasse, il faut allumer le compresseur */
	protected final double LIMIT = 2; // degres celsius
	
	/** Reference du composant associe au modele */
	protected EmbeddingComponentStateAccessI componentRef;
	
	public FrigoModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		this.setLogger(new StandardLogger());
		this.rgNewVariationTemperature = new RandomDataGenerator();
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
		
		this.currentPower = CONSOMMAION_REPOS;
		this.currentState = ModeFrigo.LIGHT_OFF;	
		this.currentTemperature = AMBIENT_TEMPERATURE;
		this.compresseur = false;
		
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
			return new Duration(1.0, TimeUnit.SECONDS);
		}
	}
	
	@Override
	public void userDefinedInternalTransition(Duration elapsedTime) {
		
		super.userDefinedInternalTransition(elapsedTime) ;

		double delta_t = elapsedTime.getSimulatedDuration() ;
		
		this.powerPlotter.addData(SERIES_POWER, this.getCurrentStateTime().getSimulatedTime(), this.getConsommation());
		this.statePlotter.addData(SERIES_MODE, this.getCurrentStateTime().getSimulatedTime(), this.getState().getMode());
		this.temperaturePlotter.addData(SERIES_TEMPERATURE, this.getCurrentStateTime().getSimulatedTime(), this.getCurrentTemperature());

		this.computeNewLevel(this.getCurrentStateTime(), delta_t) ;
	}
	
	/** Calcule la consommation en fonction de la temperature courante */
	protected void computeNewLevel(Time current, double delta_t) {
		double variation_temperature = this.rgNewVariationTemperature.nextBeta(2, 2);
		//System.out.println(variation_temperature);
		
		if(currentState == ModeFrigo.OFF) {
			if(currentTemperature < AMBIENT_TEMPERATURE && delta_t >= 1.0) {
				currentTemperature += variation_temperature;
				delta_t--;
			}
			currentPower = 0.0;
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
					if(temperature_cible < currentTemperature + variation_temperature/4)
						break;
					currentTemperature += variation_temperature;
					delta_t--;
				}
			}
			
			currentPower = getConsommationFromTemperature(currentTemperature, temperature_cible);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	protected double getConsommationFromTemperature(double temperature, double temperature_cible) {
		if(temperature - temperature_cible >= LIMIT && !compresseur) {
			compresseur = true;
			return CONSOMMATION_EXECUTE_COMPRESSEUR + CONSOMMATION_INITIALE_COMPRESSEUR;
		} else if(temperature - temperature_cible >= LIMIT && compresseur) {
			return CONSOMMATION_EXECUTE_COMPRESSEUR;
		}
		else {
			compresseur = false;
			return CONSOMMAION_REPOS;
		}
	}

	@Override
	public void userDefinedExternalTransition(Duration elapsedTime) {
		
		Vector<EventI> currentEvents = this.getStoredEventAndReset();
		assert currentEvents != null && currentEvents.size() == 1;
		Event ce = (Event) currentEvents.get(0);
		assert ce instanceof AbstractFrigoEvent;

		ce.executeOn(this);
		
		this.powerPlotter.addData(SERIES_POWER, this.getCurrentStateTime().getSimulatedTime(), this.getConsommation());
		this.statePlotter.addData(SERIES_MODE, this.getCurrentStateTime().getSimulatedTime(), this.getState().getMode());
		this.temperaturePlotter.addData(SERIES_TEMPERATURE, this.getCurrentStateTime().getSimulatedTime(), this.getCurrentTemperature());

		super.userDefinedExternalTransition(elapsedTime);
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
			// ouverture de porte equivalent a une augmentation de la temperature du frigo
			currentTemperature += this.rgNewVariationTemperature.nextBeta(2, 2) * Math.pow(LIMIT, 3);
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
