package simulator.models.lavelinge;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import app.util.ModeLaveLinge;
import app.util.TemperatureLaveLinge;
import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentStateAccessI;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOAwithEquations;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.AbstractSimulationReport;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.utils.PlotterDescription;
import fr.sorbonne_u.utils.XYPlotter;
import simulator.events.lavelinge.AbstractLaveLingeEvent;
import simulator.events.lavelinge.SetEssorage;
import simulator.events.lavelinge.SetInternalTransition;
import simulator.events.lavelinge.SetLavage;
import simulator.events.lavelinge.SetLaveLingeVeille;
import simulator.events.lavelinge.SetRincage;
import simulator.events.lavelinge.SetSechage;
import simulator.events.lavelinge.SwitchLaveLingeOff;

@ModelExternalEvents(imported = {
		SetEssorage.class,
		SetLavage.class,
		SetLaveLingeVeille.class,
		SetRincage.class,
		SetSechage.class,
		SwitchLaveLingeOff.class,
		SetInternalTransition.class
})

public class LaveLingeModel extends AtomicHIOAwithEquations{

	private static final long serialVersionUID = 1L;
	public static final String URI = "LaveLingeModel";
	public static final String COMPONENT_REF = "lavelinge-component-ref";
	public static final String POWER_PLOTTING_PARAM_NAME = "consommation";
	private static final String SERIES_POWER = "power";
	
	protected static final double CONSOMMATION_ROTATION = 180; // Watts
	protected static final double CONSOMMATION_ESSORAGE = 260; // Watts
	protected static final double CONSOMMATION_REPOS = 10; // Watts
	protected static final double CONSOMMATION_SECHAGE = 280; // Watss

	protected double currentPower; // Watts
	protected ModeLaveLinge currentState;
	protected TemperatureLaveLinge currentTemperature; // degres celsius
	protected XYPlotter powerPlotter;
	
	protected EmbeddingComponentStateAccessI componentRef;
	
	public static class LaveLingeReport extends AbstractSimulationReport {
		private static final long serialVersionUID = 1L;

		public LaveLingeReport(String modelURI) {
			super(modelURI);
		}

		@Override
		public String toString() {
			return "LaveLingeReport(" + this.getModelURI() + ")";
		}
	}
	
	public LaveLingeModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		
		super(uri, simulatedTimeUnit, simulationEngine);
		this.setLogger(new StandardLogger());
	}
	
	@Override
	public void setSimulationRunParameters(Map<String, Object> simParams) throws Exception {
		
		this.componentRef = (EmbeddingComponentStateAccessI) simParams.get(URI + " : " + COMPONENT_REF);
		
		PlotterDescription pd = (PlotterDescription) simParams.get(URI + " : " + POWER_PLOTTING_PARAM_NAME);
		this.powerPlotter = new XYPlotter(pd);
		this.powerPlotter.createSeries(SERIES_POWER);
	}

	@Override
	public void initialiseState(Time initialTime) {
		
		this.currentPower = 0.0;
		this.currentState = ModeLaveLinge.OFF;	
		this.powerPlotter.initialise();
		this.powerPlotter.showPlotter();
		
		super.initialiseState(initialTime);
	}
	
	@Override
	protected void initialiseVariables(Time startTime) {
		this.currentPower = 0.0;
		this.powerPlotter.addData(SERIES_POWER, this.getCurrentStateTime().getSimulatedTime(), this.getConsommation());
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
			return new Duration(1.0, TimeUnit.SECONDS);
		}
	}
	
	@Override
	public void userDefinedInternalTransition(Duration elapsedTime) {
		super.userDefinedInternalTransition(elapsedTime);
		computeNewConsommation();
		this.powerPlotter.addData(SERIES_POWER, this.getCurrentStateTime().getSimulatedTime(), this.getConsommation());
	}
	
	/** Calcul de la consommation courante */
	protected void computeNewConsommation() {
		switch (currentState) {
		case OFF:
			this.currentPower = 0.0;
			break;
		case VEILLE:
			this.currentPower = CONSOMMATION_REPOS;
			break;
		case CHAUFFER_EAU:
			this.currentPower = CONSOMMATION_ROTATION + currentTemperature.getConsommation();
			break;
		case LAVAGE:
			this.currentPower = CONSOMMATION_ROTATION;
			break;
		case RINCAGE:
			this.currentPower = CONSOMMATION_ROTATION;
			break;
		case ESSORAGE:
			this.currentPower = CONSOMMATION_ESSORAGE;
			break;
		case SECHAGE:
			this.currentPower = CONSOMMATION_SECHAGE;
			break;
		default:
			// cannot happen
			break;
		}
	}
	
	@Override
	public void userDefinedExternalTransition(Duration elapsedTime) {

		Vector<EventI> currentEvents = this.getStoredEventAndReset();
		assert currentEvents != null;

		for (EventI ce : currentEvents) {
			assert ce instanceof AbstractLaveLingeEvent;
			
			this.powerPlotter.addData(SERIES_POWER, this.getCurrentStateTime().getSimulatedTime(), this.getConsommation());

			if(!(ce instanceof SetInternalTransition))
				ce.executeOn(this);

			this.powerPlotter.addData(SERIES_POWER, this.getCurrentStateTime().getSimulatedTime(), this.getConsommation());
		}
		super.userDefinedExternalTransition(elapsedTime);
	}
	
	@Override
	public void endSimulation(Time endTime) throws Exception {
		this.powerPlotter.addData(SERIES_POWER, endTime.getSimulatedTime(), this.getConsommation());
		Thread.sleep(10000L);
		this.powerPlotter.dispose();

		super.endSimulation(endTime);
	}

	@Override
	public SimulationReportI getFinalReport() throws Exception {
		return new LaveLingeReport(this.getURI());
	}
	
	public void setState(ModeLaveLinge s) {
		this.currentState = s;
	}
	
	public ModeLaveLinge getState() {
		return this.currentState;
	}

	public double getConsommation() {
		return this.currentPower;
	}
}
