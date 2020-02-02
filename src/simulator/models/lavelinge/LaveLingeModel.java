package simulator.models.lavelinge;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import app.util.ModeLaveLinge;
import app.util.TemperatureLaveLinge;
import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
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
import simulator.events.lavelinge.SendLaveLingeConsommation;

/**
 * @author Willy Nassim
 */

@ModelExternalEvents(
		exported = {
			SendLaveLingeConsommation.class
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

	/** Consommation actuelle du lave-linge */
	@ExportedVariable(type = Double.class)
	protected Value<Double> currentPower = new Value<Double>(this, 0.0, 0); // Watts
	protected ModeLaveLinge currentState;
	protected TemperatureLaveLinge currentTemperature; // degres celsius
	
	protected XYPlotter powerPlotter;
	
	protected EmbeddingComponentAccessI componentRef;
	
	/** dernier etat lu dans le composant */
	protected ModeLaveLinge lastState;
	/** vrai si la consommation a changer */
	protected boolean consumptionHasChanged ;
	
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
		
		this.componentRef = (EmbeddingComponentAccessI) simParams.get(URI + " : " + COMPONENT_REF);
		
		PlotterDescription pd = (PlotterDescription) simParams.get(URI + " : " + POWER_PLOTTING_PARAM_NAME);
		this.powerPlotter = new XYPlotter(pd);
		this.powerPlotter.createSeries(SERIES_POWER);
	}

	@Override
	public void initialiseState(Time initialTime) {
		
		this.currentPower.v = 0.0;
		this.consumptionHasChanged = false ;
		this.lastState = ModeLaveLinge.OFF;
		this.currentState = ModeLaveLinge.OFF;	
		this.powerPlotter.initialise();
		this.powerPlotter.showPlotter();
		
		super.initialiseState(initialTime);
	}
	
	@Override
	protected void initialiseVariables(Time startTime) {
		this.currentPower.v = 0.0;
		this.powerPlotter.addData(SERIES_POWER, this.getCurrentStateTime().getSimulatedTime(), this.getConsommation());
		super.initialiseVariables(startTime);
	}

	@Override
	public ArrayList<EventI> output() {
		
		if (this.consumptionHasChanged) {

			ArrayList<EventI> ret = new ArrayList<EventI>() ;
			Time t = this.getCurrentStateTime().add(getNextTimeAdvance()) ;
			try {
				ret.add(new SendLaveLingeConsommation(t,
						currentPower.v)) ;
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

		computeNewConsommation();
		
		try {
			
			this.powerPlotter.addData(SERIES_POWER, this.getCurrentStateTime().getSimulatedTime(), this.getConsommation());
			
			assert	this.componentRef != null ;
			
			ModeLaveLinge m = (ModeLaveLinge) this.componentRef.getEmbeddingComponentStateValue(LaveLingeModel.URI + " : state");
			
			// Recuperation de l'etat depuis le composant pour changer la consommation envoye au compteur
			if (m != this.lastState) {
				switch(m)
				{
					case OFF : this.setState(ModeLaveLinge.OFF) ; break ;
					case VEILLE : this.setState(ModeLaveLinge.VEILLE) ; break ;
					case LAVAGE : this.setState(ModeLaveLinge.LAVAGE) ; break;
					case RINCAGE : this.setState(ModeLaveLinge.RINCAGE) ; break;
					case ESSORAGE : this.setState(ModeLaveLinge.ESSORAGE) ; break;
					case SECHAGE : this.setState(ModeLaveLinge.SECHAGE) ; break;
					case CHAUFFER_EAU : this.setState(ModeLaveLinge.CHAUFFER_EAU) ;
				}
				this.consumptionHasChanged = true ;
				this.lastState = m ;
			}
			
			this.powerPlotter.addData(SERIES_POWER, this.getCurrentStateTime().getSimulatedTime(), this.getConsommation());
			
		
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
		
	}
	
	/** Calcul de la consommation courante */
	protected void computeNewConsommation() {
		switch (currentState) {
		case OFF:
			this.currentPower.v = 0.0;
			break;
		case VEILLE:
			this.currentPower.v = CONSOMMATION_REPOS;
			break;
		case CHAUFFER_EAU:
			this.currentPower.v = CONSOMMATION_ROTATION + currentTemperature.getConsommation();
			break;
		case LAVAGE:
			this.currentPower.v = CONSOMMATION_ROTATION;
			break;
		case RINCAGE:
			this.currentPower.v = CONSOMMATION_ROTATION;
			break;
		case ESSORAGE:
			this.currentPower.v = CONSOMMATION_ESSORAGE;
			break;
		case SECHAGE:
			this.currentPower.v = CONSOMMATION_SECHAGE;
			break;
		default:
			// cannot happen
			break;
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
		return this.currentPower.v;
	}
}
