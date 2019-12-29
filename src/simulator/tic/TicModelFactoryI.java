package simulator.tic;

import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelFactoryI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

import java.util.concurrent.TimeUnit;

public interface			TicModelFactoryI
extends		AtomicModelFactoryI
{
	public TicModelFactoryI	createTicModelAndSetDelay(
		String modelURI,
		TimeUnit simulatedTimeUnit,
		SimulatorI simulationEngine,
		double delay) ;
}
//----------------------------------------------------------------------------
