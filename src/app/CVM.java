package app;

import app.components.Assembleur;
import app.util.URI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;

public class CVM extends AbstractCVM {
	
	public static final int plotX = 480; //340; 
	public static final int plotY = 20;

	public CVM() throws Exception { 
		super();
		SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 10L;
	}
	
	@Override
	public void deploy() throws Exception {
		String[] jvm_uris = {
				AbstractCVM.thisJVMURI,
				AbstractCVM.thisJVMURI,
				AbstractCVM.thisJVMURI,
				AbstractCVM.thisJVMURI,
				AbstractCVM.thisJVMURI,
				AbstractCVM.thisJVMURI,
				AbstractCVM.thisJVMURI
		};
		
		@SuppressWarnings("unused")
		String assembleur = AbstractComponent.createComponent(
							Assembleur.class.getCanonicalName(),
							new Object[]{
									URI.DYNAMIC_ASSEMBLEUR_URI.getURI(),
									jvm_uris});
		super.deploy();
	}

	public static void main(String[] args) {
		try {
			CVM c = new CVM();
			c.startStandardLifeCycle(60000L);
			Thread.sleep(5000L);
			System.exit(0);
		} catch (Exception e) { throw new RuntimeException(e); }
	}
}