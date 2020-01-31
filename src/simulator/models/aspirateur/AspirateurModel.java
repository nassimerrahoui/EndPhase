package simulator.models.aspirateur;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import app.util.ModeAspirateur;
import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOAwithEquations;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.utils.PlotterDescription;
import fr.sorbonne_u.utils.XYPlotter;
import simulator.events.aspirateur.SendAspirateurConsommation;

@ModelExternalEvents(
		exported = {
			SendAspirateurConsommation.class
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
	
	@ExportedVariable(type = Double.class)
	protected Value<Double> currentConsommation = new Value<Double>(this, 0.0, 0); // Watts
	protected ModeAspirateur currentState;
	protected XYPlotter powerPlotter;
	protected EmbeddingComponentAccessI componentRef;
	
	/** the last value emitted as a reading of the solar solarIntensity. */
	protected ModeAspirateur lastState;
	/** vrai si la consommation a changer */
	protected boolean consumptionHasChanged ;
	

	public AspirateurModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		this.setLogger(new StandardLogger());
	}

	@Override
	public void setSimulationRunParameters(Map<String, Object> simParams) throws Exception {
		this.componentRef = (EmbeddingComponentAccessI) simParams.get(URI + " : " + COMPONENT_REF);
		
		PlotterDescription pd = (PlotterDescription) simParams.get(URI + " : " + POWER_PLOTTING_PARAM_NAME);
		
		this.powerPlotter = new XYPlotter(pd);
		this.powerPlotter.createSeries(SERIES_POWER);
	}

	@Override
	public void initialiseState(Time initialTime) {
		this.consumptionHasChanged = false ;
		this.lastState = ModeAspirateur.OFF;
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
		this.powerPlotter.addData(SERIES_POWER, this.getCurrentStateTime().getSimulatedTime(), this.getConsommation());
		super.initialiseVariables(startTime);
	}

	@Override
	public ArrayList<EventI> output() {

		if (this.consumptionHasChanged) {

			ArrayList<EventI> ret = new ArrayList<EventI>() ;
			Time t = this.getCurrentStateTime().add(getNextTimeAdvance()) ;
			try {
				ret.add(new SendAspirateurConsommation(t,
						currentConsommation.v)) ;
			} catch (Exception e) {
				throw new RuntimeException(e) ;
			}
			
			this.consumptionHasChanged = false ;
			return ret ;
			
		} else {
			return null ;
		}
		
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
		try {
			
			this.powerPlotter.addData(SERIES_POWER, this.getCurrentStateTime().getSimulatedTime(), this.getConsommation());
			
			assert	this.componentRef != null ;
			ModeAspirateur m = (ModeAspirateur) this.componentRef.getEmbeddingComponentStateValue(AspirateurModel.URI + " : state");
			if (m != this.lastState) {
				switch(m)
				{
					case OFF : this.setState(ModeAspirateur.OFF) ; break ;
					case PERFORMANCE_REDUITE : this.setState(ModeAspirateur.PERFORMANCE_REDUITE) ; break ;
					case PERFORMANCE_MAXIMALE : this.setState(ModeAspirateur.PERFORMANCE_MAXIMALE) ;
				}
				this.consumptionHasChanged = true ;
				this.lastState = m ;
			}
			
			//this.powerPlotter.addData(SERIES_POWER, this.getCurrentStateTime().getSimulatedTime(), this.getConsommation());
		
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
		
	}

	@Override
	public void userDefinedExternalTransition(Duration elapsedTime) {
		// No external imported event
	}

	@Override
	public void endSimulation(Time endTime) throws Exception {
		this.powerPlotter.addData(SERIES_POWER, endTime.getSimulatedTime(), this.getConsommation());
		Thread.sleep(10000L);
		this.powerPlotter.dispose();

		super.endSimulation(endTime);
	}
	
	/**
	 * Modifie l'etat de l'aspirateur 
	 * en mettant a jour sa consommation electrique
	 * @param s
	 */
	public void setState(ModeAspirateur s) {
		this.currentState = s;
		switch (s) {
		case OFF:
			this.currentConsommation.v = 0.0;
			break;
		case PERFORMANCE_REDUITE:
			this.currentConsommation.v = CONSOMMATION_PERFORMANCE_REDUITE;
			break;
		case PERFORMANCE_MAXIMALE:
			this.currentConsommation.v = CONSOMMATION_PERFORMANCE_MAXIMALE;
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
		return this.currentConsommation.v;
	}
}