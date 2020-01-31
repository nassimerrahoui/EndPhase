package simulator.models.lavelinge;

import java.util.HashMap;
import java.util.HashSet;
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
import simulator.events.lavelinge.SendLaveLingeConsommation;
import simulator.models.lavelinge.LaveLingeModel;
import simulator.models.lavelinge.LaveLingePlanificationModel;

public class LaveLingeCoupledModel extends CoupledModel{
	private static final long serialVersionUID = 1L;
	/** URI of the unique instance of this class (in this example). */
	public static final String URI = "LaveLingeCoupledModel";

	public LaveLingeCoupledModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine,
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

		atomicModelDescriptors.put(LaveLingeModel.URI, AtomicHIOA_Descriptor.create(LaveLingeModel.class,
				LaveLingeModel.URI, TimeUnit.SECONDS, null, SimulationEngineCreationMode.ATOMIC_ENGINE));
		atomicModelDescriptors.put(LaveLingePlanificationModel.URI, AtomicModelDescriptor.create(LaveLingePlanificationModel.class,
				LaveLingePlanificationModel.URI, TimeUnit.SECONDS, null, SimulationEngineCreationMode.ATOMIC_ENGINE));

		Map<String, CoupledModelDescriptor> coupledModelDescriptors = new HashMap<String, CoupledModelDescriptor>();

		Set<String> submodels = new HashSet<String>();
		submodels.add(LaveLingeModel.URI);
		submodels.add(LaveLingePlanificationModel.URI);

		Map<Class<? extends EventI>,ReexportedEvent> reexported =
				new HashMap<Class<? extends EventI>,ReexportedEvent>() ;
		reexported.put(SendLaveLingeConsommation.class, new ReexportedEvent(LaveLingeModel.URI, SendLaveLingeConsommation.class)) ;
		
		
		coupledModelDescriptors.put(LaveLingeCoupledModel.URI,
				new CoupledHIOA_Descriptor(LaveLingeCoupledModel.class, LaveLingeCoupledModel.URI, submodels, null,
						reexported, null, null, SimulationEngineCreationMode.COORDINATION_ENGINE, null, null, null));

		return new Architecture(LaveLingeCoupledModel.URI, atomicModelDescriptors, coupledModelDescriptors,
				TimeUnit.SECONDS);
	}
}
