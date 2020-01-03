package simulator.models.lavelinge;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import app.util.ModeLaveLinge;
import app.util.TemperatureLaveLinge;
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

	@ExportedVariable(type = Double.class)
	protected final Value<Double> currentPower = new Value<Double>(this, 0.0, 0); // Watts
	protected ModeLaveLinge currentState;
	protected TemperatureLaveLinge currentTemperature;
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
		
		this.currentState = ModeLaveLinge.OFF;	
		this.powerPlotter.initialise();
		this.powerPlotter.showPlotter();

		try {
			//this.setDebugLevel(1);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		super.initialiseState(initialTime);
	}
	
	@Override
	protected void initialiseVariables(Time startTime) {
		this.currentPower.v = 0.0;
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
			return new Duration(10.0, TimeUnit.SECONDS);
		}
	}
	
	@Override
	public void userDefinedInternalTransition(Duration elapsedTime) {
		if (this.componentRef != null) {
			try {
				this.logMessage("lave-linge state = " + componentRef.getEmbeddingComponentStateValue(URI + " : state"));
				this.logMessage("lave-linge consommation = " + componentRef.getEmbeddingComponentStateValue(URI + " : consommation"));
				currentTemperature = (TemperatureLaveLinge) componentRef.getEmbeddingComponentStateValue(URI + " : temperature");
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
	}
	
	@Override
	public void userDefinedExternalTransition(Duration elapsedTime) {
		if (this.hasDebugLevel(2)) {
			this.logMessage("LaveLingeModel::userDefinedExternalTransition 1");
		}

		Vector<EventI> currentEvents = this.getStoredEventAndReset();
		assert currentEvents != null && currentEvents.size() == 1;

		Event ce = (Event) currentEvents.get(0);
		assert ce instanceof AbstractLaveLingeEvent;
		
		System.out.println(ce);
		
		if (this.hasDebugLevel(2)) {
			this.logMessage("LaveLingeModel::userDefinedExternalTransition 2 " + ce.getClass().getCanonicalName());
		}

		this.powerPlotter.addData(SERIES_POWER, this.getCurrentStateTime().getSimulatedTime(), this.getConsommation());

		if (this.hasDebugLevel(2)) {
			this.logMessage("LaveLingeModel::userDefinedExternalTransition 3 " + this.getState());
		}

		ce.executeOn(this);

		if (this.hasDebugLevel(1)) {
			this.logMessage("LaveLingeModel::userDefinedExternalTransition 4 " + this.getState());
		}

		this.powerPlotter.addData(SERIES_POWER, this.getCurrentStateTime().getSimulatedTime(), this.getConsommation());

		super.userDefinedExternalTransition(elapsedTime);
		if (this.hasDebugLevel(2)) {
			this.logMessage("LaveLingeModel::userDefinedExternalTransition 5");
		}
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
		switch (s) {
		case OFF:
			this.currentPower.v = 0.0;
			break;
		case VEILLE:
			this.currentPower.v = 5.0;
			break;
		case LAVAGE:
			this.currentPower.v = currentTemperature.getConsommation();
			break;
		case RINCAGE:
			this.currentPower.v = currentTemperature.getConsommation();
			break;
		case ESSORAGE:
			this.currentPower.v = 50.0;
			break;
		case SECHAGE:
			this.currentPower.v = 60.0;
			break;
		default:
			// cannot happen
			break;
		}
	}
	
	public ModeLaveLinge getState() {
		return this.currentState;
	}

	public double getConsommation() {
		return this.currentPower.v;
	}
}
