package fr.sorbonne_u.components.cyphy.plugins.devs;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentAtomicModelDescriptor;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelArchitectureI;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.PortURISinkReference;
import fr.sorbonne_u.components.cyphy.plugins.devs.connectors.EventsExchangingConnector;
import fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.EventsExchangingCI;
import fr.sorbonne_u.components.cyphy.plugins.devs.ports.EventsExchangingInboundPort;
import fr.sorbonne_u.components.cyphy.plugins.devs.ports.EventsExchangingOutboundPort;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.models.events.AbstractAtomicSinkReference;
import fr.sorbonne_u.devs_simulation.models.events.AtomicSinkReference;
import fr.sorbonne_u.devs_simulation.models.events.CallableEventAtomicSink;
import fr.sorbonne_u.devs_simulation.models.events.EventI;

// -----------------------------------------------------------------------------
/**
 * The class <code>AtomicSimulatorPlugin</code> implements the behaviours
 * required by a component that holds an atomic simulation model; it acts
 * mostly as a facade for the simulation engine and model that it is holding,
 * passing them most of the calls sometimes with adaptations (glue) to
 * account for the fact that communication among simulation engines and models
 * may pass through component connections.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * An atomic simulation plug-in is meant to manage the simulation models
 * held by its component. As such, it performs operations to create the
 * simulation architecture local to the component, for the interconnection
 * of models during the creation of the global inter-components simulator
 * architecture and during the simulation runs themselves to make its
 * simulation engines and models execute the simulation steps.
 * </p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2018-04-06</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			AtomicSimulatorPlugin
extends		AbstractSimulatorPlugin
implements	AtomicSimulatorPluginI
{
	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Plug-in internal constants and variables
	// -------------------------------------------------------------------------

	protected Architecture								localArchitecture ;
	/** Port to receive events from other simulation models.				*/
	protected EventsExchangingInboundPort				eeip ;
	/** Ports map to send events to other simulation models.				*/
	protected Map<String,EventsExchangingOutboundPort>	eePorts ;

	// -------------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------------

	public				AtomicSimulatorPlugin() {
		
	}

	// -------------------------------------------------------------------------
	// Plug-in generic methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.AbstractPlugin#initialise()
	 */
	@Override
	public void			initialise() throws Exception
	{
		super.initialise() ;
		this.eePorts = new HashMap<String,EventsExchangingOutboundPort>() ;
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractPlugin#isInitialised()
	 */
	@Override
	public boolean		isInitialised() throws Exception
	{
		return super.isInitialised() && this.eePorts != null ;
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractPlugin#installOn(fr.sorbonne_u.components.ComponentI)
	 */
	@Override
	public void			installOn(ComponentI owner) throws Exception
	{
		assert	owner != null ;
		assert	this.getPluginURI() != null ;
		assert	this.isSimulationArchitectureSet() ;

		super.installOn(owner) ;

		this.addOfferedInterface(EventsExchangingCI.class) ;
		this.eeip =
			new EventsExchangingInboundPort(this.getPluginURI(), this.owner) ;
		this.eeip.publishPort() ;

		this.addRequiredInterface(EventsExchangingCI.class) ;

		this.constructSimulator() ;
	}

	/**
	 * create the simulator from the local simulation architecture and
	 * initialise the variable <code>simulator</code> with the reference
	 * to the simulator object.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @throws Exception	<i>todo</i>.
	 */
	protected void		constructSimulator() throws Exception
	{
		this.simulator =
			this.localArchitecture.constructSimulator(this.getPluginURI()) ;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.AbstractSimulatorPlugin#finalise()
	 */
	@Override
	public void			finalise() throws Exception
	{
		if (this.eePorts != null) {
			for (EventsExchangingOutboundPort eeop : this.eePorts.values()) {
				if (eeop.connected()) {
					this.owner.doPortDisconnection(eeop.getPortURI()) ;
				}
			}
		}

		
		assert	this.eePorts == null ||
					this.eePorts.values().stream().
						map(eeop -> {	try {
											return !eeop.connected();
										} catch (Exception e) {
											throw new RuntimeException(e) ;
										}
									}).
						allMatch(b -> b) ;

		super.finalise() ;
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractPlugin#uninstall()
	 */
	@Override
	public void			uninstall() throws Exception
	{
		this.eeip.unpublishPort() ;
		this.eeip.destroyPort() ;
		this.eeip = null ;
		this.removeOfferedInterface(EventsExchangingCI.class) ;

		if (this.eePorts != null) {
			for (EventsExchangingOutboundPort eeop : this.eePorts.values()) {
				eeop.unpublishPort() ;
				eeop.destroyPort() ;
			}
			this.eePorts.clear() ;
		}
		assert	this.eePorts == null || this.eePorts.isEmpty() ;
		this.removeRequiredInterface(EventsExchangingCI.class) ;

		super.uninstall() ;
	}

	// -------------------------------------------------------------------------
	// Plug-in specific methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPluginI#isSimulationArchitectureSet()
	 */
	@Override
	public boolean		isSimulationArchitectureSet()
	{
		return this.localArchitecture != null ;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPluginI#setSimulationArchitecture(fr.sorbonne_u.devs_simulation.architectures.Architecture)
	 */
	@Override
	public void			setSimulationArchitecture(Architecture archi)
	{
		assert	this.getPluginURI() != null ;
		assert	!this.isSimulationArchitectureSet() ;
		assert	archi != null ;

		this.localArchitecture = archi ;
	}

	// -------------------------------------------------------------------------
	// Model manipulation methods (e.g., description, composition, ...)
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#findProxyAtomicEngineURI(java.lang.String)
	 */
	@Override
	public String		findProxyAtomicEngineURI(String modelURI)
	throws Exception
	{
		assert	this.simulator != null ;
		
		return this.simulator.findProxyAtomicEngineURI(modelURI) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getEventAtomicSinks(java.lang.Class)
	 */
	@Override
	public Set<CallableEventAtomicSink>	getEventAtomicSinks(
		Class<? extends EventI> ce
		) throws Exception
	{
		assert	this.simulator != null ;

		Set<CallableEventAtomicSink> internals =
									this.simulator.getEventAtomicSinks(ce) ;
		Set<CallableEventAtomicSink> ret = new HashSet<>() ;
		for (CallableEventAtomicSink as : internals) {
			ret.add(new CallableEventAtomicSink(
							as.importingModelURI,
							as.sourceEventType,
							as.sinkEventType,
							new PortURISinkReference(this.eeip.getPortURI()),
							as.converter)) ;
		}
		return ret ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#addInfluencees(java.lang.String, java.lang.Class, java.util.Set)
	 */
	@Override
	public void			addInfluencees(
		String modelURI,
		Class<? extends EventI> ce,
		Set<CallableEventAtomicSink> influencees
		) throws Exception
	{
		assert	this.simulator != null ;
		assert	modelURI != null ;
		assert	this.simulator.getURI().equals(modelURI) ||
					this.simulator.isDescendentModel(modelURI) ;
		assert	ce != null ;
		assert	influencees != null && influencees.size() != 0 ;

		// When adding influencees that reside in other components, the
		// sink references must be converted to references passing through
		// an outbound port of this plug-in owner component
		Set<CallableEventAtomicSink> newInfluencees =
								new HashSet<CallableEventAtomicSink>() ;
		for (CallableEventAtomicSink caes : influencees) {
			assert !caes.importingAtomicModelReference.isDirect() ;
			String inboundPortURI =
				((PortURISinkReference)
							caes.importingAtomicModelReference).portURI ;
			EventsExchangingOutboundPort eeop = null ;
			if (this.eePorts.containsKey(inboundPortURI)) {
				// when a submodel influences a remote model that is already
				// influenced by another submodel
				eeop = this.eePorts.get(inboundPortURI) ;
			} else {
				// when this is the first submodel to influence the remote
				// model
				eeop = new EventsExchangingOutboundPort(this.owner) ;
				eeop.publishPort() ;
				this.owner.doPortConnection(
						eeop.getPortURI(),
						inboundPortURI,
						EventsExchangingConnector.class.getCanonicalName()) ;
				this.eePorts.put(inboundPortURI, eeop) ;
			}
			newInfluencees.add(new CallableEventAtomicSink(
											caes.importingModelURI,
											caes.sourceEventType,
											caes.sinkEventType,
											new AtomicSinkReference(eeop),
											caes.converter)) ;
		}
		this.simulator.addInfluencees(modelURI, ce, newInfluencees) ;
	}

	// -------------------------------------------------------------------------
	// Model simulation methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#getAtomicEngineReference(java.lang.String)
	 */
	@Override
	public AbstractAtomicSinkReference	getAtomicEngineReference(
		String atomicEngineURI
		) throws Exception
	{
		assert	this.simulator != null ;

		if (this.simulator.getURI().equals(atomicEngineURI) ||
					this.simulator.isDescendentModel(atomicEngineURI)) {
			return new PortURISinkReference(this.eeip.getPortURI()) ;
		} else {
			return null ;
		}
	}

	// ------------------------------------------------------------------------
	// Parent notification methods
	// ------------------------------------------------------------------------

	
	// ------------------------------------------------------------------------
	// Events exchanging methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.AbstractSimulatorPlugin#storeInput(java.lang.String, ArrayList)
	 */
	@Override
	public void			storeInput(
		String destinationModelURI,
		ArrayList<EventI> es
		) throws Exception
	{
		assert	this.simulator != null ;
		assert	destinationModelURI != null ;
		assert	this.simulator.getURI().equals(destinationModelURI) ||
					this.simulator.isDescendentModel(destinationModelURI) ;
		assert	es != null && !es.isEmpty() ;

		this.simulator.storeInput(destinationModelURI, es) ;
	}

	// ------------------------------------------------------------------------
	// Simulation management methods
	// ------------------------------------------------------------------------

	// ------------------------------------------------------------------------
	// Simulation model access method
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cyphy.interfaces.ModelStateAccessI#getModelStateValue(java.lang.String, java.lang.String)
	 */
	@Override
	public Object		getModelStateValue(String modelURI, String name)
	throws Exception
	{
		throw new Exception(
					"Call to the method getModelStateValue in " +
					"AtomicSimulatorPlugin that should have been " +
					"redefined in a subclass.") ;
	}

	// ------------------------------------------------------------------------
	// DEVS Atomic simulator plug-in specific methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorPluginManagementI#compose(fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelArchitectureI)
	 */
	@Override
	public String		compose(ComponentModelArchitectureI architecture)
	throws Exception
	{
		assert	architecture.isAtomicModel(this.getPluginURI()) ;

		ComponentAtomicModelDescriptor amd =
				(ComponentAtomicModelDescriptor)
						architecture.getModelDescriptor(this.getPluginURI()) ;

		assert	this.includedIn(amd.importedEvents,
								this.simulator.getImportedEventTypes()) ;
		assert	this.includedIn(this.simulator.getExportedEventTypes(),
								amd.exportedEvents) ;

		return this.sip.getPortURI() ;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.AbstractSimulatorPlugin#reinitialise()
	 */
	@Override
	public void			reinitialise() throws Exception
	{
		super.reinitialise() ;

		if (this.eePorts != null) {
			for (EventsExchangingOutboundPort eeop : this.eePorts.values()) {
				if (eeop.connected()) {
					this.owner.doPortDisconnection(eeop.getPortURI()) ;
				}
				eeop.unpublishPort() ;
				eeop.destroyPort() ;
			}
			this.eePorts.clear() ;
		}
		this.constructSimulator() ;
	}

	/**
	 * return true if every element in <code>tab1</code> appears in
	 * <code>tab2</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code tab1 != null && tab2 != null}
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param tab1	array of instances of {@code Class<?>} representing events.
	 * @param tab2	array of instances of {@code Class<?>} representing events.
	 * @return		true if every element in <code>tab1</code> appears in <code>tab2</code>.
	 */
	private boolean		includedIn(
		Class<? extends EventI>[] tab1,
		Class<? extends EventI>[] tab2
		)
	{
		assert	tab1 != null && tab2 != null ;

		boolean allAppear = true ;
		for (Class<?> c : tab1) {
			allAppear &= this.appearsIn(c, tab2) ;
		}
		return allAppear ;
	}

	/**
	 * return true in the <code>c</code> appears in <code>tab</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code c != null && tab != null}
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param c		an instance of {@code Class<?>} representing an event.
	 * @param tab	array of instances of {@code Class<?>} representing events.
	 * @return		true in the <code>c</code> appears in <code>tab</code>.
	 */
	private boolean		appearsIn(Class<?> c, Class<? extends EventI>[] tab)
	{
		assert	c != null && tab != null ;
		boolean found = false ;
		for (int i = 0 ; i < tab.length && !found ; i++) {
			found = c.equals(tab[i]) ;
		}
		return found ;
	}
}
// -----------------------------------------------------------------------------
