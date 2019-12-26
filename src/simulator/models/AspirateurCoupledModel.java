package simulator.models;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.SimulationEngineCreationMode;
import fr.sorbonne_u.devs_simulation.hioa.architectures.AtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.CoupledHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.StaticVariableDescriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.CoupledModel;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardCoupledModelReport;
import simulator.events.SetPerformanceMaximale;
import simulator.events.SetPerformanceReduite;
import simulator.events.SwitchOff;
import simulator.events.SwitchOn;

import java.util.HashMap;
import java.util.HashSet;

public class AspirateurCoupledModel extends CoupledModel {
	
	private static final long serialVersionUID = 1L;
	/** URI of the unique instance of this class (in this example). */
	public static final String URI = "AspirateurCoupledModel";

	public AspirateurCoupledModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine,
			ModelDescriptionI[] submodels, Map<Class<? extends EventI>, EventSink[]> imported,
			Map<Class<? extends EventI>, ReexportedEvent> reexported, Map<EventSource, EventSink[]> connections,
			Map<StaticVariableDescriptor, VariableSink[]> importedVars,
			Map<VariableSource, StaticVariableDescriptor> reexportedVars, Map<VariableSource, VariableSink[]> bindings)
			throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine, submodels, imported, reexported, connections, importedVars,
				reexportedVars, bindings);
	}

	@Override
	public SimulationReportI getFinalReport() throws Exception {
		StandardCoupledModelReport ret = new StandardCoupledModelReport(this.getURI());
		for (int i = 0; i < this.submodels.length; i++) {
			ret.addReport(this.submodels[i].getFinalReport());
		}
		return ret;
	}

	public static Architecture build() throws Exception {
		Map<String, AbstractAtomicModelDescriptor> atomicModelDescriptors = new HashMap<>();

		atomicModelDescriptors.put(AspirateurModel.URI, AtomicHIOA_Descriptor.create(AspirateurModel.class,
				AspirateurModel.URI, TimeUnit.SECONDS, null, SimulationEngineCreationMode.ATOMIC_ENGINE));
		atomicModelDescriptors.put(AspirateurUserModel.URI, AtomicModelDescriptor.create(AspirateurUserModel.class,
				AspirateurUserModel.URI, TimeUnit.SECONDS, null, SimulationEngineCreationMode.ATOMIC_ENGINE));

		Map<String, CoupledModelDescriptor> coupledModelDescriptors = new HashMap<String, CoupledModelDescriptor>();

		Set<String> submodels = new HashSet<String>();
		submodels.add(AspirateurModel.URI);
		submodels.add(AspirateurUserModel.URI);

		Map<EventSource, EventSink[]> connections = new HashMap<EventSource, EventSink[]>();
		EventSource from1 = new EventSource(AspirateurUserModel.URI, SwitchOn.class);
		EventSink[] to1 = new EventSink[] { new EventSink(AspirateurModel.URI, SwitchOn.class) };
		connections.put(from1, to1);
		EventSource from2 = new EventSource(AspirateurUserModel.URI, SwitchOff.class);
		EventSink[] to2 = new EventSink[] { new EventSink(AspirateurModel.URI, SwitchOff.class) };
		connections.put(from2, to2);
		EventSource from3 = new EventSource(AspirateurUserModel.URI, SetPerformanceReduite.class);
		EventSink[] to3 = new EventSink[] { new EventSink(AspirateurModel.URI, SetPerformanceReduite.class) };
		connections.put(from3, to3);
		EventSource from4 = new EventSource(AspirateurUserModel.URI, SetPerformanceMaximale.class);
		EventSink[] to4 = new EventSink[] { new EventSink(AspirateurModel.URI, SetPerformanceMaximale.class) };
		connections.put(from4, to4);

		coupledModelDescriptors.put(AspirateurCoupledModel.URI,
				new CoupledHIOA_Descriptor(AspirateurCoupledModel.class, AspirateurCoupledModel.URI, submodels, null,
						null, connections, null, SimulationEngineCreationMode.COORDINATION_ENGINE, null, null, null));

		return new Architecture(AspirateurCoupledModel.URI, atomicModelDescriptors, coupledModelDescriptors,
				TimeUnit.SECONDS);
	}
}