package simulator.models.batterie;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import app.util.EtatUniteProduction;
import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOAwithEquations;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
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
import simulator.events.batterie.SendBatterieProduction;

@ModelExternalEvents(
		exported = {
			SendBatterieProduction.class
		})

public class BatterieModel extends AtomicHIOAwithEquations {

	public static class BatterieStateReport extends AbstractSimulationReport {
		private static final long serialVersionUID = 1L;

		public BatterieStateReport(String modelURI) {
			super(modelURI);
		}

		@Override
		public String toString() {
			return "BatterieStateReport(" + this.getModelURI() + ")";
		}
	}
	
	private static final long serialVersionUID = 1L;
	public static final String URI = "BatterieModel";
	public static final String COMPONENT_REF = "batterie-component-ref";
	public static final String PRODUCTION_PLOTTING_PARAM_NAME = "energy production";
	
	private static final String SERIES_PRODUCTION = "production";
	protected Value<Double> currentProduction = new Value<Double>(this, 0.0, 0); // Watts
	protected EtatUniteProduction currentState;
	
	protected static final double ON_PRODUCTION = 150;
	
	protected XYPlotter powerPlotter;
	protected EmbeddingComponentAccessI componentRef;
	
	public BatterieModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		
		super(uri, simulatedTimeUnit, simulationEngine);
		this.setLogger(new StandardLogger());
	}

	@Override
	public void setSimulationRunParameters(Map<String, Object> simParams) throws Exception {
		
		this.componentRef = (EmbeddingComponentAccessI) simParams.get(URI + " : " + COMPONENT_REF);

		PlotterDescription pd = (PlotterDescription) simParams.get(URI + " : " + PRODUCTION_PLOTTING_PARAM_NAME);
		this.powerPlotter = new XYPlotter(pd);
		this.powerPlotter.createSeries(SERIES_PRODUCTION);
	}

	@Override
	public void initialiseState(Time initialTime) {
		this.currentProduction.v = 0.0;
		this.currentState = EtatUniteProduction.ON;	
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
		
		this.powerPlotter.addData(SERIES_PRODUCTION, this.getCurrentStateTime().getSimulatedTime(), this.getEnergy());
		super.initialiseVariables(startTime);
	}

	@Override
	public ArrayList<EventI> output() {
		ArrayList<EventI> ret = new ArrayList<EventI>() ;
		Time t = this.getCurrentStateTime().add(getNextTimeAdvance()) ;
		
		try {
			ret.add(new SendBatterieProduction(t, currentProduction.v));
			
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
		
		return ret ;
		
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
		if (this.componentRef != null) {
			try {
				this.setState((EtatUniteProduction) componentRef.getEmbeddingComponentStateValue(URI + " : state"));
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		
		super.userDefinedInternalTransition(elapsedTime);
		this.powerPlotter.addData(SERIES_PRODUCTION, this.getCurrentStateTime().getSimulatedTime(), this.getEnergy());
	}

	@Override
	public void userDefinedExternalTransition(Duration elapsedTime) {
		// No external event imported
	}

	@Override
	public void endSimulation(Time endTime) throws Exception {
		this.powerPlotter.addData(SERIES_PRODUCTION, endTime.getSimulatedTime(), this.getEnergy());
		Thread.sleep(10000L);
		this.powerPlotter.dispose();

		super.endSimulation(endTime);
	}

	@Override
	public SimulationReportI getFinalReport() throws Exception {
		return new BatterieStateReport(this.getURI());
	}

	/**
	 * Change l'etat de la batterie en mettant
	 * a jour la production electrique
	 * @param s
	 */
	public void setState(EtatUniteProduction s) {
		this.currentState = s;
		switch (s) {
		case OFF:
			this.currentProduction.v = 0.0;
			break;
		case ON:
			this.currentProduction.v = ON_PRODUCTION;
			break;
		default:
			// cannot happen
			break;
		}
	}

	public EtatUniteProduction getState() {
		return this.currentState;
	}

	public double getEnergy() {
		return this.currentProduction.v;
	}
}