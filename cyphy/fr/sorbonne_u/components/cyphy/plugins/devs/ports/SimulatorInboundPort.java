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

import java.util.Map;
import java.util.ArrayList;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.events.AbstractAtomicSinkReference;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

// -----------------------------------------------------------------------------
/**
 * The class <code>SimulatorInboundPort</code> implements the inbound
 * port for the offered interface <code>SimulatorCI</code>.
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
public class			SimulatorInboundPort
extends		AbstractModelDescriptionInboundPort
implements	SimulatorCI
{
	private static final long serialVersionUID = 1L;

	public				SimulatorInboundPort(
		String uri,
		Class<?> implementedInterface,
		String pluginURI,
		ComponentI owner
		) throws Exception
	{
		super(uri, implementedInterface, pluginURI, owner) ;

		assert	SimulatorI.class.isAssignableFrom(implementedInterface) ;
	}

	public				SimulatorInboundPort(
		Class<?> implementedInterface,
		String pluginURI,
		ComponentI owner
		) throws Exception
	{
		super(implementedInterface, pluginURI, owner) ;

		assert	SimulatorI.class.isAssignableFrom(implementedInterface) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#findProxyAtomicEngineURI(java.lang.String)
	 */
	@Override
	public String		findProxyAtomicEngineURI(String modelURI)
	throws Exception
	{
		return this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<String>(this.pluginURI) {
					@Override
					public String call() throws Exception {
						return ((SimulatorI)
									this.getServiceProviderReference()).
										findProxyAtomicEngineURI(modelURI) ;
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#getAtomicEngineReference(java.lang.String)
	 */
	@Override
	public AbstractAtomicSinkReference		getAtomicEngineReference(
		String atomicEngineURI
		) throws Exception
	{
		return this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<
							AbstractAtomicSinkReference>(this.pluginURI) {
					@Override
					public AbstractAtomicSinkReference call()
					throws Exception
					{	
						return ((SimulatorI)
								this.getServiceProviderReference()).
										getAtomicEngineReference(
														atomicEngineURI) ;
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ParentNotificationI#hasReceivedExternalEvents(java.lang.String)
	 */
	@Override
	public void			hasReceivedExternalEvents(String modelURI)
	throws Exception
	{
		this.owner.handleRequestAsync(
				new AbstractComponent.AbstractService<Void>(this.pluginURI) {
					@Override
					public Void call() throws Exception {
						((SimulatorI)
								this.getServiceProviderReference()).
										hasReceivedExternalEvents(modelURI) ;
						return null ;
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ParentNotificationI#hasPerformedExternalEvents(java.lang.String)
	 */
	@Override
	public void			hasPerformedExternalEvents(String modelURI)
	throws Exception
	{
		this.owner.handleRequestAsync(
				new AbstractComponent.AbstractService<Void>(this.pluginURI) {
					@Override
					public Void call() throws Exception {
						((SimulatorI)
								this.getServiceProviderReference()).
										hasPerformedExternalEvents(modelURI) ;
						return null ;
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.EventsExchangingI#storeInput(java.lang.String, ArrayList)
	 */
	@Override
	public void			storeInput(String destinationURI, ArrayList<EventI> es)
	throws Exception
	{
		this.owner.handleRequestAsync(
				new AbstractComponent.AbstractService<Void>(this.pluginURI) {
					@Override
					public Void call() throws Exception {
						((SimulatorI)
									this.getServiceProviderReference()).
											storeInput(destinationURI, es) ;
						return null ;
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#getTimeOfStart()
	 */
	@Override
	public Time			getTimeOfStart() throws Exception
	{
		return this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Time>(this.pluginURI) {
					@Override
					public Time call() throws Exception {
						return ((SimulatorI)
									this.getServiceProviderReference()).
														getTimeOfStart() ;
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#getSimulationEndTime()
	 */
	@Override
	public Time			getSimulationEndTime() throws Exception
	{
		return this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Time>(this.pluginURI) {
					@Override
					public Time call() throws Exception {
						return ((SimulatorI)
									this.getServiceProviderReference()).
													getSimulationEndTime() ;
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#doStandAloneSimulation(double, double)
	 */
	@Override
	public void			doStandAloneSimulation(
		double startTime,
		double simulationDuration
		) throws Exception
	{
		this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Void>(this.pluginURI) {
					@Override
					public Void call() throws Exception {
						((SimulatorI)
									this.getServiceProviderReference()).
										doStandAloneSimulation(startTime,
													  simulationDuration) ;
						return null ;
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#startRealTimeSimulation(double, double)
	 */
	@Override
	public void			startRealTimeSimulation(
		double startTime,
		double simulationDuration
		) throws Exception
	{
		this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Void>(this.pluginURI) {
					@Override
					public Void call() throws Exception {
						((SimulatorI)
									this.getServiceProviderReference()).
										startRealTimeSimulation(
														startTime,
														simulationDuration) ;
						return null ;
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#isSimulationRunning()
	 */
	@Override
	public boolean		isSimulationRunning() throws Exception
	{
		return this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Boolean>(this.pluginURI) {
					@Override
					public Boolean call() throws Exception {
						return ((SimulatorI)
									this.getServiceProviderReference()).
													isSimulationRunning() ;
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#stopSimulation()
	 */
	@Override
	public void			stopSimulation() throws Exception
	{
		this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Void>(this.pluginURI) {
					@Override
					public Void call() throws Exception {
						((SimulatorI)
									this.getServiceProviderReference()).
														stopSimulation() ;
						return null ;
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#getFinalReport()
	 */
	@Override
	public SimulationReportI		getFinalReport() throws Exception
	{
		return this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<SimulationReportI>(this.pluginURI) {
					@Override
					public SimulationReportI call() throws Exception {
						return ((SimulatorI)
									this.getServiceProviderReference()).
														getFinalReport() ;
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#finaliseSimulation()
	 */
	@Override
	public void			finaliseSimulation() throws Exception
	{
		this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Void>(this.pluginURI) {
					@Override
					public Void call() throws Exception {
						((SimulatorI)
									this.getServiceProviderReference()).
														finaliseSimulation() ;
						return null ;
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#setSimulatedModel(fr.sorbonne_u.devs_simulation.models.interfaces.ModelI)
	 */
	@Override
	public void			setSimulatedModel(ModelI simulatedModel)
	throws Exception
	{
		this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Void>(this.pluginURI) {
					@Override
					public Void call() throws Exception {
						((SimulatorI)
								this.getServiceProviderReference()).
										setSimulatedModel(simulatedModel) ;
						return null ;
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#isModelSet()
	 */
	@Override
	public boolean		isModelSet() throws Exception
	{
		return this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Boolean>(this.pluginURI) {
					@Override
					public Boolean call() throws Exception {
						return ((SimulatorI)
									this.getServiceProviderReference()).
														isModelSet() ;
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void				setSimulationRunParameters(
		Map<String,
		Object> simParams
		) throws Exception
	{
		this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Void>(this.pluginURI) {
					@Override
					public Void call() throws Exception
					{
						((SimulatorI)
							this.getServiceProviderReference()).
									setSimulationRunParameters(simParams) ;
						return null ;
					}
				}) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#initialiseSimulation(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			initialiseSimulation(Duration simulationDuration)
	throws Exception
	{
		this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Void>(this.pluginURI) {
					@Override
					public Void call() throws Exception {
						((SimulatorI)
								this.getServiceProviderReference()).
									initialiseSimulation(simulationDuration) ;
						return null ;
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#initialiseSimulation(fr.sorbonne_u.devs_simulation.models.time.Time, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			initialiseSimulation(
		Time startTime,
		Duration simulationDuration
		) throws Exception
	{
		this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Void>(this.pluginURI) {
					@Override
					public Void call() throws Exception {
						((SimulatorI)
								this.getServiceProviderReference()).
									initialiseSimulation(
											startTime, simulationDuration) ;
						return null ;
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#isSimulationInitialised()
	 */
	@Override
	public boolean		isSimulationInitialised() throws Exception
	{
		return this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Boolean>(this.pluginURI) {
					@Override
					public Boolean call() throws Exception {
						return ((SimulatorI)
									this.getServiceProviderReference()).
												isSimulationInitialised() ;
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#internalEventStep()
	 */
	@Override
	public void			internalEventStep() throws Exception
	{
		this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Void>(this.pluginURI) {
					@Override
					public Void call() throws Exception {
						((SimulatorI)
									this.getServiceProviderReference()).
													internalEventStep() ;
						return null ;
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#causalEventStep(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			causalEventStep(Time current) throws Exception
	{
		this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Void>(this.pluginURI) {
					@Override
					public Void call() throws Exception {
						((SimulatorI) this.getServiceProviderReference()).
													causalEventStep(current) ;
						return null ;
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#externalEventStep(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			externalEventStep(Duration elapsedTime)
	throws Exception
	{
		this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Void>(this.pluginURI) {
					@Override
					public Void call() throws Exception {
						((SimulatorI)
									this.getServiceProviderReference()).
											externalEventStep(elapsedTime) ;
						return null ;
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#confluentEventStep(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			confluentEventStep(Duration elapsedTime)
	throws Exception
	{
		this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Void>(this.pluginURI) {
					@Override
					public Void call() throws Exception {
						((SimulatorI)
									this.getServiceProviderReference()).
											confluentEventStep(elapsedTime) ;
						return null ;
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#produceOutput(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			produceOutput(Time current) throws Exception
	{
		this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Void>(this.pluginURI) {
					@Override
					public Void call() throws Exception {
						((SimulatorI)
									this.getServiceProviderReference()).
													produceOutput(current) ;
						return null ;
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void		endSimulation(Time endTime) throws Exception
	{
		this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Void>(this.pluginURI) {
					@Override
					public Void call() throws Exception {
						((SimulatorI) this.getServiceProviderReference()).
													endSimulation(endTime) ;
						return null ;
					}
				});		
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#getTimeOfLastEvent()
	 */
	@Override
	public Time			getTimeOfLastEvent() throws Exception
	{
		return this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Time>(this.pluginURI) {
					@Override
					public Time call() throws Exception {
						return ((SimulatorI)
									this.getServiceProviderReference()).
													getTimeOfLastEvent() ;
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#getTimeOfNextEvent()
	 */
	@Override
	public Time			getTimeOfNextEvent() throws Exception
	{
		return this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Time>(this.pluginURI) {
					@Override
					public Time call() throws Exception {
						return ((SimulatorI)
									this.getServiceProviderReference()).
													getTimeOfNextEvent() ;
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#getNextTimeAdvance()
	 */
	@Override
	public Duration		getNextTimeAdvance() throws Exception
	{
		return this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Duration>(this.pluginURI) {
					@Override
					public Duration call() throws Exception {
						return ((SimulatorI)
									this.getServiceProviderReference()).
													getNextTimeAdvance() ;
					}
				}) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#toggleDebugMode()
	 */
	@Override
	public void			toggleDebugMode() throws Exception
	{
		this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Void>(this.pluginURI) {
					@Override
					public Void call() throws Exception {
						((SimulatorI) this.getServiceProviderReference()).
														toggleDebugMode() ;
						return null;
					}
				}) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#isDebugModeOn()
	 */
	@Override
	public boolean		isDebugModeOn() throws Exception
	{
		return this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Boolean>(this.pluginURI) {
					@Override
					public Boolean call() throws Exception {
						return ((SimulatorI)
									this.getServiceProviderReference()).
														isDebugModeOn() ;
					}
				}) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#setDebugLevel(int)
	 */
	@Override
	public void			setDebugLevel(int newDebugLevel) throws Exception
	{
		this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Void>(this.pluginURI) {
					@Override
					public Void call() throws Exception {
						((SimulatorI) this.getServiceProviderReference()).
											setDebugLevel(newDebugLevel) ;
						return null;
					}
				}) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#hasDebugLevel(int)
	 */
	@Override
	public boolean		hasDebugLevel(int debugLevel) throws Exception
	{
		return this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Boolean>(this.pluginURI) {
					@Override
					public Boolean call() throws Exception {
						return ((SimulatorI)
									this.getServiceProviderReference()).
											hasDebugLevel(debugLevel) ;
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#showCurrentStateContent(java.lang.String, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			showCurrentStateContent(
		String indent,
		Duration elapsedTime
		) throws Exception
	{
		this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Void>(this.pluginURI) {
					@Override
					public Void call() throws Exception {
						((SimulatorI) this.getServiceProviderReference()).
								showCurrentStateContent(indent, elapsedTime) ;
						return null;
					}
				});		
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#showCurrentState(java.lang.String, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			showCurrentState(String indent, Duration elapsedTime)
	throws Exception
	{
		this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Void>(this.pluginURI) {
					@Override
					public Void call() throws Exception {
						((SimulatorI) this.getServiceProviderReference()).
								showCurrentState(indent, elapsedTime) ;
						return null ;
					}
				});		
	}
}
// -----------------------------------------------------------------------------
