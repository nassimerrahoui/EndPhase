package app.components;

import app.interfaces.assembleur.IComposantDynamique;
import app.ports.coordinator.CoordinatorAssembleurInPort;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.ports.PortI;

@OfferedInterfaces(offered = { IComposantDynamique.class })

/**
 * Le coordinateur est utilise par le supervisor pour coordonnees les echanges d'evenement entre les architectures
 * @author Willy Nassim
 *
 */
public class Coordinator extends AbstractCyPhyComponent {

	protected Coordinator() throws Exception {
		super(2, 0);
		
		// port entrant permettant a l'assembleur de deployer le composant
		CoordinatorAssembleurInPort launch_INPORT = new CoordinatorAssembleurInPort(this);
		launch_INPORT.publishPort();
		
		this.initialise();
	}

	protected Coordinator(String reflectionInboundPortURI) throws Exception {
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

	/**
	 * Initialise le plugin
	 */
	protected void initialise() {
		this.tracer.setTitle("Smart grid coupled model component");
		this.tracer.setRelativePosition(1, 4);
		this.toggleTracing();
	}

	/**
	 * Execution depuis l'assembleur
	 * @throws Exception
	 */
	public void dynamicExecute() throws Exception {
		super.execute();
		this.logMessage("Coordinator component begins execution.");
	}
}