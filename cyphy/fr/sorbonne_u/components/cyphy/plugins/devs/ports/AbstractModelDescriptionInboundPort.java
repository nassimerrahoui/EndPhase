package fr.sorbonne_u.components.cyphy.plugins.devs.ports;

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

import java.util.Set;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelDescriptionCI;
import fr.sorbonne_u.components.ports.forplugins.AbstractInboundPortForPlugin;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.StaticVariableDescriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.interfaces.EventsExchangingI;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import fr.sorbonne_u.devs_simulation.interfaces.ParentNotificationI;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.ParentReferenceI;
import fr.sorbonne_u.devs_simulation.models.events.CallableEventAtomicSink;
import fr.sorbonne_u.devs_simulation.models.events.EventAtomicSource;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;

// -----------------------------------------------------------------------------
/**
 * The class <code>AbstractModelDescriptionInboundPort</code> implements the
 * inbound port for the offered interface <code>ModelDescriptionCI</code>.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2018-06-01</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public abstract class	AbstractModelDescriptionInboundPort
extends		AbstractInboundPortForPlugin
implements	ModelDescriptionCI
{
	private static final long serialVersionUID = 1L;

	public				AbstractModelDescriptionInboundPort(
		String uri,
		Class<?> implementedInterface,
		String pluginURI,
		ComponentI owner
		) throws Exception
	{
		super(uri, implementedInterface, pluginURI, owner) ;

		assert	ModelDescriptionI.class.
								isAssignableFrom(implementedInterface) ;
	}

	public				AbstractModelDescriptionInboundPort(
		Class<?> implementedInterface,
		String pluginURI,
		ComponentI owner
		) throws Exception
	{
		super(implementedInterface, pluginURI, owner);

		assert	ModelDescriptionI.class.
								isAssignableFrom(implementedInterface) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getURI()
	 */
	@Override
	public String		getURI() throws Exception
	{
		return this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<String>(this.pluginURI) {
					@Override
					public String call() throws Exception {
						return ((ModelDescriptionI)
									this.getServiceProviderReference()).
																getURI() ;
					}
				}) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getSimulatedTimeUnit()
	 */
	@Override
	public TimeUnit		getSimulatedTimeUnit() throws Exception
	{
		return this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<TimeUnit>(this.pluginURI) {
					@Override
					public TimeUnit call() throws Exception {
						return ((ModelDescriptionI)
								this.getServiceProviderReference()).
													getSimulatedTimeUnit() ;
					}
				}) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getParentURI()
	 */
	@Override
	public String		getParentURI() throws Exception
	{
		return this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<String>(this.pluginURI) {
					@Override
					public String call() throws Exception {
						return ((ModelDescriptionI)
									this.getServiceProviderReference()).
													getParentURI() ;
					}
				}) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#isParentSet()
	 */
	@Override
	public boolean		isParentSet() throws Exception
	{
		return this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Boolean>(this.pluginURI) {
					@Override
					public Boolean call() throws Exception {
						return ((ModelDescriptionI)
									this.getServiceProviderReference()).
																isParentSet() ;
					}
				}) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#setParent(fr.sorbonne_u.devs_simulation.models.ParentReferenceI)
	 */
	@Override
	public void			setParent(ParentReferenceI p) throws Exception
	{
		this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Void>(this.pluginURI) {
					@Override
					public Void call() throws Exception {
						((ModelDescriptionI)
							this.getServiceProviderReference()).setParent(p) ;
						return null ;
					}
				}) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getParent()
	 */
	@Override
	public ParentNotificationI	getParent() throws Exception
	{
		return this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<ParentNotificationI>(this.pluginURI) {
					@Override
					public ParentNotificationI call() throws Exception {
						return ((ModelDescriptionI)
									this.getServiceProviderReference()).
																getParent() ;
					}
				}) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getDescendentModel(java.lang.String)
	 */
	@Override
	public ModelI		getDescendentModel(String uri) throws Exception
	{
		throw new Exception("The method getDescendentModel should not be" +
							" called through a plug-in as it may cause " +
							" a reference leak to a non-RMI Java object.") ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#isRoot()
	 */
	@Override
	public boolean		isRoot() throws Exception
	{
		return this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Boolean>(this.pluginURI) {
					@Override
					public Boolean call() throws Exception {
						return ((ModelDescriptionI)
									this.getServiceProviderReference()).
													isRoot() ;
					}
				}) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#closed()
	 */
	@Override
	public boolean		closed() throws Exception
	{
		return this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Boolean>(this.pluginURI) {
					@Override
					public Boolean call() throws Exception {
						return ((ModelDescriptionI)
									this.getServiceProviderReference()).
													closed() ;
					}
				}) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getImportedEventTypes()
	 */
	@Override
	public Class<? extends EventI>[]		getImportedEventTypes()
	throws Exception
	{
		return this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Class<? extends EventI>[]>(this.pluginURI) {
					@Override
					public Class<? extends EventI>[] call() throws Exception {
						return ((ModelDescriptionI)
								this.getServiceProviderReference()).
													getImportedEventTypes() ;
					}
				}) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#isImportedEventType(java.lang.Class)
	 */
	@Override
	public boolean		isImportedEventType(Class<? extends EventI> ec)
	throws Exception
	{
		return this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Boolean>(this.pluginURI) {
					@Override
					public Boolean call() throws Exception {
						return ((ModelDescriptionI)
									this.getServiceProviderReference()).
													isImportedEventType(ec) ;
					}
				}) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getExportedEventTypes()
	 */
	@Override
	public Class<? extends EventI>[]		getExportedEventTypes()
	throws Exception
	{
		return this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Class<? extends EventI>[]>(this.pluginURI) {
					@Override
					public Class<? extends EventI>[] call() throws Exception {
						return ((ModelDescriptionI)
								this.getServiceProviderReference()).
													getExportedEventTypes() ;
					}
				}) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#isExportedEventType(java.lang.Class)
	 */
	@Override
	public boolean		isExportedEventType(Class<? extends EventI> ec)
	throws Exception
	{
		return this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Boolean>(this.pluginURI) {
					@Override
					public Boolean call() throws Exception {
						return ((ModelDescriptionI)
									this.getServiceProviderReference()).
													isExportedEventType(ec) ;
					}
				}) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#isDescendentModel(java.lang.String)
	 */
	@Override
	public boolean		isDescendentModel(String uri) throws Exception
	{
		return this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Boolean>(this.pluginURI) {
					@Override
					public Boolean call() throws Exception {
						return ((ModelDescriptionI)
									this.getServiceProviderReference()).
													isDescendentModel(uri) ;
					}
				}) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getEventExchangingDescendentModel(java.lang.String)
	 */
	@Override
	public EventsExchangingI	getEventExchangingDescendentModel(String uri) throws Exception
	{
		return this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<EventsExchangingI>(this.pluginURI) {
					@Override
					public EventsExchangingI call() throws Exception {
						return ((ModelDescriptionI)
									this.getServiceProviderReference()).
													getEventExchangingDescendentModel(uri) ;
					}
				}) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getEventAtomicSource(java.lang.Class)
	 */
	@Override
	public EventAtomicSource		getEventAtomicSource(
		Class<? extends EventI> ce
		) throws Exception
	{
		return this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<EventAtomicSource>(this.pluginURI) {
					@Override
					public EventAtomicSource call() throws Exception {
						return ((ModelDescriptionI)
									this.getServiceProviderReference()).
													getEventAtomicSource(ce) ;
					}
				}) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getEventAtomicSinks(java.lang.Class)
	 */
	@Override
	public Set<CallableEventAtomicSink>		getEventAtomicSinks(
		Class<? extends EventI> ce
		) throws Exception
	{
		return this.owner.handleRequestSync(
					new AbstractComponent.
							AbstractService<Set<CallableEventAtomicSink>>(
															this.pluginURI) {
						@Override
						public Set<CallableEventAtomicSink> call()
						throws Exception
						{
							return ((ModelDescriptionI)
									this.getServiceProviderReference()).
													getEventAtomicSinks(ce) ;
						}
					}) ;
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
		this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Void>(this.pluginURI) {
					@Override
					public Void call() throws Exception {
						((ModelDescriptionI)
							this.getServiceProviderReference()).
									addInfluencees(modelURI, ce, influencees) ;
						return null ;
					}
				}) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getInfluencees(java.lang.String, java.lang.Class)
	 */
	@Override
	public Set<CallableEventAtomicSink>		getInfluencees(
		String modelURI,
		Class<? extends EventI> ce
		) throws Exception
	{
		return this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<
							Set<CallableEventAtomicSink>>(this.pluginURI) {
					@Override
					public Set<CallableEventAtomicSink> call()
					throws Exception
					{
							return ((ModelDescriptionI)
									this.getServiceProviderReference()).
											getInfluencees(modelURI, ce) ;
					}
				}) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#areInfluencedThrough(java.lang.String, java.util.Set, java.lang.Class)
	 */
	@Override
	public boolean		areInfluencedThrough(
		String modelURI,
		Set<String> destinationModelURIs,
		Class<? extends EventI> ce
		) throws Exception
	{
		return this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Boolean>(this.pluginURI) {
					@Override
					public Boolean call() throws Exception {
							return ((ModelDescriptionI)
									this.getServiceProviderReference()).
											areInfluencedThrough(
													modelURI,
													destinationModelURIs,
													ce) ;
					}
				}) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#isInfluencedThrough(java.lang.String, java.lang.String, java.lang.Class)
	 */
	@Override
	public boolean		isInfluencedThrough(
		String modelURI,
		String destinationModelURI,
		Class<? extends EventI> ce
		) throws Exception
	{
		return this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Boolean>(this.pluginURI) {
					@Override
					public Boolean call() throws Exception {
						return ((ModelDescriptionI)
									this.getServiceProviderReference()).
											isInfluencedThrough(
													modelURI,
													destinationModelURI,
													ce) ;
					}
				}) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#isTIOA()
	 */
	@Override
	public boolean		isTIOA() throws Exception
	{
		return this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Boolean>(
														this.pluginURI) {
					@Override
					public Boolean call() throws Exception {
						return ((ModelDescriptionI)
								this.getServiceProviderReference()).isTIOA() ;
					}
				}) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#isOrdered()
	 */
	@Override
	public boolean		isOrdered() throws Exception
	{
		return this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Boolean>(
														this.pluginURI) {
					@Override
					public Boolean call() throws Exception {
						return ((ModelDescriptionI)
									this.getServiceProviderReference()).
															isOrdered() ;
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#isExportedVariable(java.lang.String, java.lang.Class)
	 */
	@Override
	public boolean		isExportedVariable(String name, Class<?> type)
	throws Exception
	{
		return this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Boolean>(
														this.pluginURI) {
					@Override
					public Boolean call() throws Exception {
						return ((ModelDescriptionI)
									this.getServiceProviderReference()).
											isExportedVariable(name, type) ;
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#isImportedVariable(java.lang.String, java.lang.Class)
	 */
	@Override
	public boolean		isImportedVariable(String name, Class<?> type)
	throws Exception
	{
		return this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Boolean>(
														this.pluginURI) {
					@Override
					public Boolean call() throws Exception {
						return ((ModelDescriptionI)
								this.getServiceProviderReference()).
											isImportedVariable(name, type) ;
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getImportedVariables()
	 */
	@Override
	public StaticVariableDescriptor[]	getImportedVariables()
	throws Exception
	{
		return this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<
								StaticVariableDescriptor[]>(this.pluginURI) {
					@Override
					public StaticVariableDescriptor[] call() throws Exception
					{
						return ((ModelDescriptionI)
								this.getServiceProviderReference()).
													getImportedVariables() ;
					}
				}) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getExportedVariables()
	 */
	@Override
	public StaticVariableDescriptor[]	getExportedVariables()
	throws Exception
	{
		return this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<
								StaticVariableDescriptor[]>(this.pluginURI) {
					@Override
					public StaticVariableDescriptor[] call() throws Exception
					{
						return ((ModelDescriptionI)
								this.getServiceProviderReference()).
													getExportedVariables() ;
					}
				}) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getActualExportedVariableValueReference(java.lang.String, java.lang.String, java.lang.Class)
	 */
	@Override
	public Value<?>			getActualExportedVariableValueReference(
		String modelURI,
		String sourceVariableName,
		Class<?> sourceVariableType
		) throws Exception
	{
		return this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<
				Value<?>>(this.pluginURI) {
					@Override
					public Value<?> call() throws Exception
					{
						return ((ModelDescriptionI)
								this.getServiceProviderReference()).
									getActualExportedVariableValueReference(
														modelURI,
														sourceVariableName,
														sourceVariableType) ;
					}
				}) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#setImportedVariableValueReference(java.lang.String, java.lang.String, java.lang.Class, fr.sorbonne_u.devs_simulation.hioa.models.vars.Value)
	 */
	@Override
	public void				setImportedVariableValueReference(
		String modelURI,
		String sinkVariableName,
		Class<?> sinkVariableType,
		Value<?> value
		) throws Exception
	{
		this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Void>(this.pluginURI) {
					@Override
					public Void call() throws Exception
					{
						((ModelDescriptionI)
							this.getServiceProviderReference()).
								setImportedVariableValueReference(
														modelURI,
														sinkVariableName,
														sinkVariableType,
														value) ;
						return null ;
					}
				}) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#setDebugLevel(int)
	 */
	@Override
	public void				setDebugLevel(int newDebugLevel)
	throws Exception
	{
		this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Void>(this.pluginURI) {
					@Override
					public Void call() throws Exception
					{
						((ModelDescriptionI)
								this.getServiceProviderReference()).
											setDebugLevel(newDebugLevel) ;
						return null ;
					}
				}) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getFinalReport()
	 */
	@Override
	public SimulationReportI		getFinalReport() throws Exception
	{
		return this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<SimulationReportI>(this.pluginURI) {
					@Override
					public SimulationReportI	 call() throws Exception {
						return ((ModelDescriptionI)
									this.getServiceProviderReference()).
													getFinalReport() ;
					}
				}) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#modelAsString(java.lang.String)
	 */
	@Override
	public String			modelAsString(String indent) throws Exception
	{
		return this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<String>(this.pluginURI) {
					@Override
					public String	 call() throws Exception {
						return ((ModelDescriptionI)
									this.getServiceProviderReference()).
													modelAsString(indent) ;
					}
				}) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#simulatorAsString()
	 */
	@Override
	public String			simulatorAsString() throws Exception
	{
		return this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<String>(this.pluginURI) {
					@Override
					public String	 call() throws Exception {
						return ((ModelDescriptionI)
								this.getServiceProviderReference()).
													simulatorAsString() ;
					}
				}) ;
	}
}
// -----------------------------------------------------------------------------
