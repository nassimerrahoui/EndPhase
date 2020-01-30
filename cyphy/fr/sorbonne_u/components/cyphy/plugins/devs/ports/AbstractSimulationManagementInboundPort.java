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
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI;
import fr.sorbonne_u.components.ports.forplugins.AbstractInboundPortForPlugin;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI;

// -----------------------------------------------------------------------------
/**
 * The class <code>AbstractSimulationManagementInboundPort</code> implements the
 * inbound port for the offered interface <code>SimulationManagementCI</code>.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2018-06-27</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public abstract class	AbstractSimulationManagementInboundPort
extends		AbstractInboundPortForPlugin
implements	SimulationManagementCI
{
	private static final long serialVersionUID = 1L;

	public				AbstractSimulationManagementInboundPort(
		String uri,
		Class<?> implementedInterface,
		String pluginURI,
		ComponentI owner
		) throws Exception
	{
		super(uri, implementedInterface, pluginURI, owner) ;

		assert SimulationManagementCI.class.
								isAssignableFrom(implementedInterface) ;
	}

	public				AbstractSimulationManagementInboundPort(
		Class<?> implementedInterface,
		String pluginURI, 
		ComponentI owner
		) throws Exception
	{
		super(implementedInterface, pluginURI, owner);

		assert SimulationManagementCI.class.
								isAssignableFrom(implementedInterface) ;
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
						return ((SimulationManagementI)
									this.getServiceProviderReference()).
														getTimeOfStart() ;
					}
				}) ;
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
						return ((SimulationManagementI)
									this.getServiceProviderReference()).
													getSimulationEndTime() ;
					}
				}) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws Exception
	{
		this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Void>(this.pluginURI) {
					@Override
					public Void call() throws Exception {
						((SimulationManagementI)
								this.getServiceProviderReference()).
										setSimulationRunParameters(simParams) ;
						return null ;
					}
				}) ;
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
						((SimulationManagementI)
								this.getServiceProviderReference()).
										doStandAloneSimulation(
											startTime, simulationDuration) ;
						return null;
					}
				}) ;
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
						((SimulationManagementI)
								this.getServiceProviderReference()).
									startRealTimeSimulation(
														startTime,
														simulationDuration) ;
						return null;
					}
				}) ;
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
						return ((SimulationManagementI)
									this.getServiceProviderReference()).
														isSimulationRunning() ;
					}
				}) ;
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
						((SimulationManagementI)
									this.getServiceProviderReference()).
														stopSimulation() ;
						return null;
					}
				}) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#getFinalReport()
	 */
	@Override
	public SimulationReportI		getFinalReport() throws Exception
	{
		return this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<SimulationReportI>(
															this.pluginURI) {
					@Override
					public SimulationReportI call() throws Exception {
						return ((SimulationManagementI)
									this.getServiceProviderReference()).
														getFinalReport() ;
					}
				}) ;
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
						((SimulationManagementI)
									this.getServiceProviderReference()).
													finaliseSimulation() ;
						return null;
					}
				}) ;
	}
}
//-----------------------------------------------------------------------------
