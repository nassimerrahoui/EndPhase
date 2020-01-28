package fr.sorbonne_u.components.cyphy.plugins.devs.architectures;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide an extension
// of the BCM component model that aims to define a components tailored for
// cyber-physical control systems (CPCS) for Java.
//
// This software is governed by the CeCILL-C license under French law and
// abiding by the rules of distribution of free software.  You can use,
// modify and/ or redistribute the software under the terms of the
// CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
// URL "http://www.cecill.info".
//
// As a counterpart to the access to the source code and  rights to copy,
// modify and redistribute granted by the license, users are provided only
// with a limited warranty  and the software's author,  the holder of the
// economic rights,  and the successive licensors  have only  limited
// liability. 
//
// In this respect, the user's attention is drawn to the risks associated
// with loading,  using,  modifying and/or developing or reproducing the
// software by the user in light of its specific status of free software,
// that may mean  that it is complicated to manipulate,  and  that  also
// therefore means  that it is reserved for developers  and  experienced
// professionals having in-depth computer knowledge. Users are therefore
// encouraged to load and test the software's suitability as regards their
// requirements in conditions enabling the security of their systems and/or 
// data to be ensured and,  more generally, to use and operate it in the 
// same conditions as regards security. 
//
// The fact that you are presently reading this means that you have had
// knowledge of the CeCILL-C license and that you accept its terms.

import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cyphy.plugins.devs.AbstractSimulatorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.CoordinatorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorPluginManagementCI;
import fr.sorbonne_u.components.cyphy.plugins.devs.ports.SimulatorPluginManagementOutboundPort;
import fr.sorbonne_u.components.reflection.interfaces.ReflectionI;
import fr.sorbonne_u.devs_simulation.architectures.SimulationEngineCreationMode;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelFactoryI;
import fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelI;

// -----------------------------------------------------------------------------
/**
 * The class <code>ComponentCoupledModelDescriptor</code>implements a descriptor
 * of a DEVS coupled models that is held by a BCM component in a BCM component
 * assembly, associating their URI, their static information including the
 * mapping to the holding component in the assembly, as well as an optional
 * factory that can be used to instantiate a coupled model object to run
 * simulations.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * When integrating DEVS simulation models with BCM components, a simulation
 * architecture (DEVS models composition) is mapped onto a component assembly.
 * Cyber-physical components have simulation models implementing their
 * behavioural model. At the component assembly level, a global simulation
 * architecture describes the way component simulation models are composed to
 * obtain a system-wide simulation architecture. The present class is used in
 * global architectures to describe coupled simulation models that will be
 * attached to components playing the role of simulation coordinators (in terms
 * of DEVS simulation execution).
 * </p>
 * <p>
 * Contrary to cyber-physical components, components having a coordinator role
 * do not create neither their coupled model nor the BCM DEVS coordination
 * plug-in factoring the coordination code. The present class provides methods
 * to create them on the coordinator component. Hence, as a descriptor, it is a
 * serializable to be sent by RMI to the coordinator component by the
 * supervisor one, and when received on the coordinator component, the creator
 * component can be set on the descriptor before calling the method
 * <code>compose</code> that will connect the coordinator component to the
 * components holding the submodels and then create the coupled model.
 * </p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2019-06-19</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			ComponentCoupledModelDescriptor
extends		CoupledModelDescriptor
implements	ComponentModelDescriptorI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long				serialVersionUID = 1L;
	/** URI of the reflection inbound port of the component holding the
	 *  model.																*/
	protected final String					componentReflectionInboundPortURI ;
	/** the reference to the object representing the component that is
	 *  holding the model.													*/
	transient protected AbstractComponent	creator ;
	/** the coordination plug-in installed in the creator component.		*/
	transient protected CoordinatorPlugin	creatorCoordinatorPlugin ;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * instantiate a component coupled model descriptor.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	modelClass != null
	 * pre	modelURI != null
	 * pre	{@code submodelURIs != null and submodelURIs.size() > 1}
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param modelClass			class defining the coupled model.
	 * @param modelURI				URI of the coupled model to be created.
	 * @param submodelURIs			URIs of the submodels of the coupled model.
	 * @param imported				map from imported event types to the submodels importing them.
	 * @param reexported			map from event types exported by submodels to exported event types exported by the coupled model.
	 * @param connections			connections between event types exported by submodels to event types imported by other submodels.
	 * @param cmFactory				coupled model factory allowing to create the coupled model or null if none.
	 * @param engineCreationMode	creation mode for the simulation engine.	
	 * @param compReflIBPURI		URI of the coupled model holding coordinator component reflection inbound port.
	 * @param mc					model composer for this component coupled model composition.
	 */
	protected			ComponentCoupledModelDescriptor(
		Class<? extends CoupledModelI> modelClass,
		String modelURI,
		Set<String> submodelURIs,
		Map<Class<? extends EventI>, EventSink[]> imported,
		Map<Class<? extends EventI>, ReexportedEvent> reexported,
		Map<EventSource, EventSink[]> connections,
		CoupledModelFactoryI cmFactory,
		SimulationEngineCreationMode engineCreationMode,
		String compReflIBPURI,
		ComponentModelComposer mc
		)
	{
		super(modelClass, modelURI, submodelURIs, imported, reexported,
			  connections, cmFactory, engineCreationMode, mc);

		assert	compReflIBPURI != null ;

		this.componentReflectionInboundPortURI = compReflIBPURI ;
	}

	/**
	 * create a component coupled model descriptor.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition. TODO
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param modelClass			class defining the coupled model.
	 * @param modelURI				URI of the coupled model to be created.
	 * @param submodelURIs			URIs of the submodels of the coupled model.
	 * @param imported				map from imported event types to the submodels importing them.
	 * @param reexported			map from event types exported by submodels to exported event types exported by the coupled model.
	 * @param connections			connections between event types exported by submodels to event types imported by other submodels.
	 * @param cmFactory				coupled model factory allowing to create the coupled model or null if none.
	 * @param engineCreationMode	creation mode for the simulation engine.	
	 * @param compReflIBPURI		URI of the coupled model holding coordinator component reflection inbound port.
	 * @return						the new component coupled model descriptor.
	 */
	public static ComponentCoupledModelDescriptor	create(
		Class<? extends CoupledModelI> modelClass,
		String modelURI,
		Set<String> submodelURIs,
		Map<Class<? extends EventI>, EventSink[]> imported,
		Map<Class<? extends EventI>, ReexportedEvent> reexported,
		Map<EventSource, EventSink[]> connections,
		CoupledModelFactoryI cmFactory,
		SimulationEngineCreationMode engineCreationMode,
		String compReflIBPURI
		)
	{
		assert	modelURI != null ;
		assert	compReflIBPURI != null :
					new Exception(modelURI + " has no associated component!") ;

		if (imported == null) {
			imported = new HashMap<Class<? extends EventI>, EventSink[]>() ;
		}
		if (reexported == null) {
			reexported =
				new HashMap<Class<? extends EventI>, ReexportedEvent>() ;
		}
		if (connections == null) {
			connections = new HashMap<EventSource, EventSink[]>() ;
		}
		return new ComponentCoupledModelDescriptor(
								modelClass, modelURI, submodelURIs,
								imported, reexported, connections,
								cmFactory, engineCreationMode,
								compReflIBPURI, new ComponentModelComposer()) ;
	}

	// -------------------------------------------------------------------------
	// Instance methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelDescriptorI#getComponentReflectionInboundPortURI()
	 */
	@Override
	public String		getComponentReflectionInboundPortURI()
	{
		return this.componentReflectionInboundPortURI ;
	}

	/**
	 * return true if the reference to the component that will create the
	 * model is set.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	true if the reference to the component that will create the model is set.
	 */
	public boolean		isCreatorComponentSet()
	{
		return this.creator != null ;
	}

	/**
	 * sets the reference to the component that will create the model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	!this.isCreatorComponentSet()
	 * pre	creator != null
	 * pre	creator.isRequiredInterface(ReflectionI.class)
	 * pre	creator.isRequiredInterface(SimulatorPluginManagementCI.class)
	 * pre	creator.isRequiredInterface(SimulatorCI.class)
	 * post	this.isCreatorComponentSet()
	 * </pre>
	 *
	 * @param creator	the reference to the component that will create the model.
	 * @param plugin	the plug-in that manages the simulation for the creator component.
	 */
	public void			setCreatorComponent(
		AbstractComponent creator,
		AbstractSimulatorPlugin plugin
		)
	{
		assert	!this.isCreatorComponentSet() ;
		assert	creator != null ;
		assert	creator.isRequiredInterface(ReflectionI.class) ;
		assert	creator.isRequiredInterface(SimulatorPluginManagementCI.class) ;
		assert	plugin != null ;
		assert	plugin instanceof CoordinatorPlugin ;

		this.creator = creator ;
		this.creatorCoordinatorPlugin = (CoordinatorPlugin) plugin ;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelDescriptorI#compose(fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelArchitectureI, java.lang.String)
	 */
	@Override
	public ModelDescriptionI	compose(
		ComponentModelArchitectureI architecture,
		String creatorPnipURI
		) throws Exception
	{
		assert	this.isCreatorComponentSet() ;
		assert	creatorPnipURI != null ;

		// set the parent notification inbound port URI to pass it to the
		// submodels in order for the components holding them to connect to
		// this component properly for parent notifications when creating
		// the coupled model per se.
		assert	this.mc instanceof ComponentModelComposer ;
		((ComponentModelComposer)this.mc).
						setParentNotificationInboundPortURI(creatorPnipURI) ;

		ModelDescriptionI[] models =
							new ModelDescriptionI[this.submodelURIs.size()] ;
		int i = 0 ;
		for(String submodelURI : this.submodelURIs) {
			ComponentModelDescriptorI d =
								architecture.getModelDescriptor(submodelURI) ;
			// connect this component to the component holding the submodel
			// through the simulation plug-in management interface.
			SimulatorPluginManagementOutboundPort smop =
				this.creatorCoordinatorPlugin.connectSubmodel4Management(
						submodelURI, d.getComponentReflectionInboundPortURI()) ;
			// compose recursively the submodel, receiving the URI of the
			// simulation inbound port of the component holding it in return.
			String sipURI = smop.compose(architecture) ;
			// connect this component to the component holding the submodel
			// through the simulation interface.
			models[i++] = this.creatorCoordinatorPlugin.
							connectSubmodel4Simulation(submodelURI, sipURI) ;
		}

		// create the coupled model as in plain simulation architectures
		// using the model composer as mentioned above.
		ModelDescriptionI ret = super.createCoupledModel(models) ;
		return ret ;
	}
}
// -----------------------------------------------------------------------------
