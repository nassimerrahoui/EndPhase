package simulator.tic;

import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.devs_simulation.simulators.AtomicEngine;

//------------------------------------------------------------------------------
public class TestTicModel {

	public static void main(String[] args) {
		try {
			AtomicEngine e = new AtomicEngine() ;
			new TicModel(TicModel.URI, TimeUnit.SECONDS, e) ;
			e.doStandAloneSimulation(0.0, 620.0) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
}
//------------------------------------------------------------------------------
