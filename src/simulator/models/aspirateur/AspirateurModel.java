package simulator.models.aspirateur;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import app.util.ModeAspirateur;
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
import simulator.events.aspirateur.AbstractAspirateurEvent;
import simulator.events.aspirateur.SetPerformanceMaximale;
import simulator.events.aspirateur.SetPerformanceReduite;
import simulator.events.aspirateur.SwitchAspirateurOff;
import simulator.events.aspirateur.SwitchAspirateurOn;

@ModelExternalEvents(imported = { 
		SwitchAspirateurOn.class, 
		SwitchAspirateurOff.class, 
		SetPerformanceReduite.class,
		SetPerformanceMaximale.class
		})

public class AspirateurModel extends AtomicHIOAwithEquations {

	private static final long serialVersionUID = 1L;
	public static final String URI = "AspirateurModel";
	public static final String COMPONENT_REF = "aspirateur-component-ref";
	public static final String POWER_PLOTTING_PARAM_NAME = "consommation";
	
	private static final String SERIES_POWER = "power";
	protected static final double CONSOMMATION_PERFORMANCE_REDUITE = 800.0/3.6; // Watts
	protected static final double CONSOMMATION_PERFORMANCE_MAXIMALE = 1200.0/3.6; // Watts
	protected static final double TENSION = 220.0; // Volts
	protected double currentConsommation; // Watts
	protected double currentEnergy; // Watts
	protected ModeAspirateur currentState;
	protected XYPlotter powerPlotter;
	protected EmbeddingComponentStateAccessI componentRef;
	
	public static class AspirateurReport extends AbstractSimulationReport {
		private static final long serialVersionUID = 1L;

		public AspirateurReport(String modelURI) {
			super(modelURI);
		}

		@Override
		public String toString() {
			return "AspirateurReport(" + this.getModelURI() + ")";
		}
	}

	public AspirateurModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
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
		this.currentState = ModeAspirateur.OFF;	
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
		this.currentConsommation = 0.0;
		/** TODO a remplacer */
		this.currentEnergy = 10000;
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
		this.powerPlotter.addData(SERIES_POWER, this.getCurrentStateTime().getSimulatedTime(), this.getConsommation());
		super.userDefinedInternalTransition(elapsedTime) ;
	}

	@Override
	public void userDefinedExternalTransition(Duration elapsedTime) {
		
		Vector<EventI> currentEvents = this.getStoredEventAndReset();
		assert currentEvents != null && currentEvents.size() == 1;

		Event ce = (Event) currentEvents.get(0);
		assert ce instanceof AbstractAspirateurEvent;
		
		this.powerPlotter.addData(SERIES_POWER, this.getCurrentStateTime().getSimulatedTime(), this.getConsommation());
		
		if(ce instanceof SwitchAspirateurOn || ce instanceof SetPerformanceReduite) {
			if(CONSOMMATION_PERFORMANCE_REDUITE <= this.currentEnergy)
				ce.executeOn(this);
		} else if(ce instanceof SetPerformanceMaximale) {
			if(CONSOMMATION_PERFORMANCE_MAXIMALE <= this.currentEnergy)
				ce.executeOn(this);
		} else if(ce instanceof SwitchAspirateurOff) {
			ce.executeOn(this);
		}
		
		this.powerPlotter.addData(SERIES_POWER, this.getCurrentStateTime().getSimulatedTime(), this.getConsommation());
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
		return new AspirateurReport(this.getURI());
	}

	public void setState(ModeAspirateur s) {
		this.currentState = s;
		switch (s) {
		case OFF:
			this.currentConsommation = 0.0;
			break;
		case PERFORMANCE_REDUITE:
			this.currentConsommation = CONSOMMATION_PERFORMANCE_REDUITE;
			break;
		case PERFORMANCE_MAXIMALE:
			this.currentConsommation = CONSOMMATION_PERFORMANCE_MAXIMALE;
			break;
		default:
			// cannot happen
			break;
		}
	}

	public ModeAspirateur getState() {
		return this.currentState;
	}

	public double getConsommation() {
		return this.currentConsommation;
	}
}