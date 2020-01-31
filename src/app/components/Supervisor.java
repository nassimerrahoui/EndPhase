package app.components;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import app.interfaces.assembleur.IComposantDynamique;
import app.ports.supervisor.SupervisorAssembleurInPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.cyphy.plugins.devs.SupervisorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentAtomicModelDescriptor;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentCoupledModelDescriptor;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelArchitecture;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.ports.PortI;
import fr.sorbonne_u.devs_simulation.architectures.SimulationEngineCreationMode;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import simulator.events.aspirateur.SendAspirateurConsommation;
import simulator.events.frigo.SendFrigoConsommation;
import simulator.models.aspirateur.AspirateurCoupledModel;
import simulator.models.compteur.CompteurModel;
import simulator.models.frigo.FrigoCoupledModel;
import simulator.models.supervisor.SupervisorCoupledModel;

/** Supervise l'architecture globale des modeles et les echanges d'evenements */
@OfferedInterfaces(offered = { IComposantDynamique.class })
public class Supervisor extends AbstractComponent {

	/** the supervisor plug-in attached to this component. */
	protected SupervisorPlugin sp;
	
	/**
	 * maps from URIs of models to URIs of the reflection inbound ports of the
	 * components that hold them.
	 */
	protected Map<String, String> modelURIs2componentURIs;

	protected Supervisor(Map<String, String> modelURIs2componentURIs)
			throws Exception {
		super(2, 0);

		// port entrant permettant a l'assembleur de deployer le composant
		SupervisorAssembleurInPort launch_INPORT = new SupervisorAssembleurInPort(this);
		launch_INPORT.publishPort();
		
		assert modelURIs2componentURIs != null;
		assert modelURIs2componentURIs.size() >= 1;

		this.initialise(modelURIs2componentURIs);
	}

	protected Supervisor(String reflectionInboundPortURI, Map<String, String> modelURIs2componentURIs) throws Exception {
		super(reflectionInboundPortURI, 2, 0);
		
		// port entrant permettant a l'assembleur de deployer le composant
		SupervisorAssembleurInPort launch_INPORT = new SupervisorAssembleurInPort(this);
		launch_INPORT.publishPort();

		assert modelURIs2componentURIs != null;
		assert modelURIs2componentURIs.size() >= 1;

		this.initialise(modelURIs2componentURIs);
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

	protected void initialise(Map<String, String> modelURIs2componentURIs) throws Exception {
		
		this.modelURIs2componentURIs = modelURIs2componentURIs;
		this.tracer.setRelativePosition(0, 4);
		this.toggleTracing();
		this.sp = new SupervisorPlugin(this.createSILArchitecture());
		sp.setPluginURI("supervisor");
		this.installPlugin(this.sp);
		this.logMessage("Supervisor plug-in installed...");
	}

	
	/**
	 * create the SIL simulation architecture supervised by this component.
	 */
	@SuppressWarnings("unchecked")
	protected ComponentModelArchitecture createSILArchitecture() throws Exception {
		Map<String, AbstractAtomicModelDescriptor> atomicModelDescriptors = new HashMap<>();

		// import consommation/production du compteur
		atomicModelDescriptors.put(CompteurModel.URI,
				ComponentAtomicModelDescriptor.create(CompteurModel.URI,
						(Class<? extends EventI>[]) new Class<?>[] { 
							SendAspirateurConsommation.class,
							SendFrigoConsommation.class}, 
						null,
						TimeUnit.SECONDS, this.modelURIs2componentURIs.get(CompteurModel.URI)));
		// export consommation aspirateur
		atomicModelDescriptors.put(AspirateurCoupledModel.URI,
				ComponentAtomicModelDescriptor.create(AspirateurCoupledModel.URI,
						null, (Class<? extends EventI>[]) new Class<?>[] { SendAspirateurConsommation.class }, 
						TimeUnit.SECONDS,
						this.modelURIs2componentURIs.get(AspirateurCoupledModel.URI)));
		// export consommation frigo
		atomicModelDescriptors.put(FrigoCoupledModel.URI,
				ComponentAtomicModelDescriptor.create(FrigoCoupledModel.URI,
						null, (Class<? extends EventI>[]) new Class<?>[] { SendFrigoConsommation.class }, 
						TimeUnit.SECONDS,
						this.modelURIs2componentURIs.get(FrigoCoupledModel.URI)));
		
		Map<String, CoupledModelDescriptor> coupledModelDescriptors = new HashMap<>();

		Set<String> submodels = new HashSet<String>();
		submodels.add(CompteurModel.URI);
		submodels.add(AspirateurCoupledModel.URI);
		submodels.add(FrigoCoupledModel.URI);

		Map<EventSource, EventSink[]> connections = new HashMap<EventSource, EventSink[]>();
		connections.put(new EventSource(AspirateurCoupledModel.URI, SendAspirateurConsommation.class),
				new EventSink[] { new EventSink(CompteurModel.URI, SendAspirateurConsommation.class) });
		connections.put(new EventSource(FrigoCoupledModel.URI, SendFrigoConsommation.class),
				new EventSink[] { new EventSink(CompteurModel.URI, SendFrigoConsommation.class) });

		coupledModelDescriptors.put(SupervisorCoupledModel.URI,
				ComponentCoupledModelDescriptor.create(
						SupervisorCoupledModel.class, 
						SupervisorCoupledModel.URI, 
						submodels, null, null,
						connections, null, SimulationEngineCreationMode.COORDINATION_ENGINE,
						this.modelURIs2componentURIs.get(SupervisorCoupledModel.URI)));

		ComponentModelArchitecture arch = new ComponentModelArchitecture(
				"SIL",
				SupervisorCoupledModel.URI, 
				atomicModelDescriptors, 
				coupledModelDescriptors, TimeUnit.SECONDS);

		return arch;
	}

	public void dynamicExecute() throws Exception {
		super.execute();

		this.logMessage("supervisor component begins execution.");
		this.sp.createSimulator();		
		Thread.sleep(1000L);
		this.logMessage("supervisor component begins simulation.");
		long start = System.currentTimeMillis();
		this.sp.setSimulationRunParameters(new HashMap<String, Object>());
		this.sp.doStandAloneSimulation(0, 50000.0);
		long end = System.currentTimeMillis();
		this.logMessage("supervisor component ends simulation. " + (end - start));
		Thread.sleep(1000);
	}
}