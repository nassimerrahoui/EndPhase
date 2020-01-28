package app.components;

import app.interfaces.assembleur.IComposantDynamique;
import app.ports.coordinator.CoordinatorAssembleurInPort;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.ports.PortI;

@OfferedInterfaces(offered = { IComposantDynamique.class })
public class Coordinator extends AbstractCyPhyComponent {

	protected Coordinator() throws Exception {
		// a coordinator needs to have 2 threads, one to execute the simulator
		// and the other to receive parent notifications from the submodels.
		super(2, 0);
		
		// port entrant permettant a l'assembleur de deployer le composant
		CoordinatorAssembleurInPort launch_INPORT = new CoordinatorAssembleurInPort(this);
		launch_INPORT.publishPort();
		
		this.initialise();
	}

	protected Coordinator(String reflectionInboundPortURI) throws Exception {
		// a coordinator needs to have 2 threads, one to execute the simulator
		// and the other to receive parent notifications from the submodels.
		super(reflectionInboundPortURI, 2, 0);
		
		// port entrant permettant a l'assembleur de deployer le composant
		CoordinatorAssembleurInPort launch_INPORT = new CoordinatorAssembleurInPort(this);
		launch_INPORT.publishPort();
		
		this.initialise();
	}
	
	@Override
	public void	shutdown() throws ComponentShutdownException
	{
		try {
			PortI[] port_assembleur = this.findPortsFromInterface(IComposantDynamique.class);
			port_assembleur[0].unpublishPort();
		} catch (Exception e) { throw new ComponentShutdownException(e); }
		
		super.shutdown();
	}

	@Override
	public void shutdownNow() throws ComponentShutdownException
	{
		try {
			PortI[] port_assembleur = this.findPortsFromInterface(IComposantDynamique.class);
			port_assembleur[0].unpublishPort();
		} catch (Exception e) { throw new ComponentShutdownException(e); }
		super.shutdownNow();
	}

	protected void initialise() {
		this.tracer.setTitle("Smart grid coupled model component");
		this.tracer.setRelativePosition(1, 4);
		this.toggleTracing();
	}

	public void dynamicExecute() throws Exception {
		super.execute();
		this.logMessage("Coordinator component begins execution.");
	}
}