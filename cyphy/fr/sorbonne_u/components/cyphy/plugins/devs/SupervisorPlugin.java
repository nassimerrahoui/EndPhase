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

import java.util.Map;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelArchitectureI;
import fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorPluginManagementCI;
import fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SupervisorNotificationCI;
import fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SupervisorPluginManagementCI;
import fr.sorbonne_u.components.cyphy.plugins.devs.ports.SimulatorPluginManagementOutboundPort;
import fr.sorbonne_u.components.cyphy.plugins.devs.ports.SupervisorNotificationInboundPort;
import fr.sorbonne_u.components.cyphy.plugins.devs.ports.SupervisorPluginManagementInboundPort;
import fr.sorbonne_u.components.reflection.connectors.ReflectionConnector;
import fr.sorbonne_u.components.reflection.interfaces.ReflectionI;
import fr.sorbonne_u.components.reflection.ports.ReflectionOutboundPort;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

// -----------------------------------------------------------------------------
/**
 * The class <code>SupervisorPlugin</code> implements the role of DEVS
 * simulation supervision for BCML components as a plug-in.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2018-06-13</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			SupervisorPlugin
extends		AbstractPlugin
implements	SupervisorPluginI
{
	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Plug-in internal constants and variables
	// -------------------------------------------------------------------------

	/** the global simulation architecture associated to the plug-in.		*/
	protected ComponentModelArchitectureI			architecture ;
	/** port through which other components can manage the simulations. 	*/
	protected SupervisorPluginManagementInboundPort	smip ;
	/** port through which simulators can notify back their report after
	 *  each simulation run.												*/
	protected SupervisorNotificationInboundPort		snip ;
	/** simulation management outbound port of the root model component
	 *  allowing this supervisor component to manage the simulation runs.	*/
	protected SimulatorPluginManagementOutboundPort	rootModelSmop ;
	/** variable in which the simulation report can be found after each
	 *  simulation run.														*/
	protected SimulationReportI						report ;

	// -------------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------------

	/**
	 * create a supervisor plug-in with the given global simulation
	 * architecture.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	architecture != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param architecture	architecture of the simulation model to be created.
	 * @throws Exception	<i>to do.</i>
	 */
	public				SupervisorPlugin(
		ComponentModelArchitectureI architecture
		) throws Exception
	{
		assert	architecture != null ;
		this.architecture = architecture ;		
	}

	// -------------------------------------------------------------------------
	// Plug-in generic methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.AbstractPlugin#installOn(fr.sorbonne_u.components.ComponentI)
	 */
	@Override
	public void			installOn(ComponentI owner) throws Exception
	{
		assert	owner != null ;
		assert	this.getPluginURI() != null ;

		super.installOn(owner) ;

		if (!owner.isRequiredInterface(ReflectionI.class)) {
			this.addRequiredInterface(ReflectionI.class) ;
		}
		this.addOfferedInterface(SupervisorNotificationCI.class) ;
		this.addOfferedInterface(SupervisorPluginManagementCI.class) ;
		this.addRequiredInterface(SimulatorPluginManagementCI.class) ;
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractPlugin#initialise()
	 */
	@Override
	public void			initialise() throws Exception
	{
		super.initialise() ;

		this.snip = new SupervisorNotificationInboundPort(this.getPluginURI(),
														  this.owner) ;
		this.snip.publishPort() ;

		this.smip = new SupervisorPluginManagementInboundPort(
													this.getPluginURI(),
													owner) ;
		this.smip.publishPort() ;
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractPlugin#isInitialised()
	 */
	@Override
	public boolean		isInitialised() throws Exception
	{
		return super.isInitialised() &&
							this.snip != null && this.smip != null ;
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractPlugin#finalise()
	 */
	@Override
	public void			finalise() throws Exception
	{
		if (this.rootModelSmop != null && this.rootModelSmop.connected()) {
			this.owner.doPortDisconnection(this.rootModelSmop.getPortURI()) ;
		}
		super.finalise() ;
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractPlugin#uninstall()
	 */
	@Override
	public void			uninstall() throws Exception
	{
		this.snip.unpublishPort() ;
		this.snip.destroyPort() ;
		this.removeOfferedInterface(SupervisorNotificationCI.class) ;

		this.smip.unpublishPort() ;
		this.smip.destroyPort() ;
		this.removeOfferedInterface(SupervisorPluginManagementCI.class) ;

		if (this.rootModelSmop != null) {
			this.rootModelSmop.unpublishPort() ;
			this.rootModelSmop.destroyPort() ;
		}
 		this.removeRequiredInterface(SimulatorPluginManagementCI.class) ;

		super.uninstall();
	}

	// -------------------------------------------------------------------------
	// Plug-in specific methods
	// -------------------------------------------------------------------------

	/**
	 * return the outbound port connected to the root model component, or
	 * null if none is connected.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	the simulation management reference of the root model, or null if none is connected.
	 */
	protected SimulatorPluginManagementOutboundPort	getRootSmop()
	{
		return this.rootModelSmop ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#getTimeOfStart()
	 */
	@Override
	public Time			getTimeOfStart() throws Exception
	{
		return this.getRootSmop().getTimeOfStart() ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#getSimulationEndTime()
	 */
	@Override
	public Time			getSimulationEndTime() throws Exception
	{
		return this.getRootSmop().getSimulationEndTime() ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws Exception
	{
		assert	simParams != null ;

		this.getRootSmop().setSimulationRunParameters(simParams) ;
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
		this.getRootSmop().doStandAloneSimulation(
											startTime, simulationDuration) ;
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
		this.getRootSmop().startRealTimeSimulation(
										startTime, simulationDuration) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#isSimulationRunning()
	 */
	@Override
	public boolean		isSimulationRunning() throws Exception
	{
		return this.getRootSmop().isSimulationRunning() ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#stopSimulation()
	 */
	@Override
	public void			stopSimulation() throws Exception
	{
		this.getRootSmop().stopSimulation() ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#getFinalReport()
	 */
	@Override
	public SimulationReportI	getFinalReport() throws Exception
	{
		return this.getRootSmop().getFinalReport() ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#finaliseSimulation()
	 */
	@Override
	public void			finaliseSimulation() throws Exception
	{
		this.getRootSmop().finaliseSimulation() ;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SupervisorPluginManagementI#connectRootSimulatorComponent()
	 */
	@Override
	public void			connectRootSimulatorComponent()
	throws Exception
	{
		this.logMessage("connecting supervisor to " +
										this.architecture.getRootModelURI()) ;

		this.rootModelSmop = this.architecture.connectRootModelComponent(
											(AbstractComponent)this.owner) ;
		this.rootModelSmop.connectSupervision(this.snip.getPortURI()) ;
		this.logMessage(this.architecture.getRootModelURI() + " connected.") ;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SupervisorPluginManagementI#createSimulator()
	 */
	public void			createSimulator() throws Exception
	{
		assert	this.architecture != null && this.architecture.isComplete() ;

		if (this.rootModelSmop == null) {
			this.connectRootSimulatorComponent() ;
		}

		// TODO: does not work for simulation architectures containing only
		// one atomic model that is root model by default.
		this.logMessage("composing...") ;
		this.getRootSmop().compose(architecture) ;
		this.logMessage("... done composing") ;		
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SupervisorPluginManagementI#resetArchitecture(fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelArchitectureI)
	 */
	@Override
	public void			resetArchitecture(
		ComponentModelArchitectureI architecture
		) throws Exception
	{
		assert	architecture != null ;
		
		this.rootModelSmop.reinitialise() ;
		this.owner.doPortDisconnection(this.rootModelSmop.getPortURI()) ;
		this.rootModelSmop.unpublishPort() ;
		this.rootModelSmop.destroyPort() ;
		this.rootModelSmop = null ;
		ReflectionOutboundPort rop = new ReflectionOutboundPort(this.owner) ;
		rop.publishPort() ;
		this.owner.doPortConnection(
				rop.getPortURI(),
				this.architecture.getReflectionInboundPortURI(
										this.architecture.getRootModelURI()),
				ReflectionConnector.class.getCanonicalName()) ;
		rop.uninstallPlugin(this.architecture.getRootModelURI()) ;
		this.owner.doPortDisconnection(rop.getPortURI()) ;
		rop.unpublishPort() ;
		rop.destroyPort() ;
		rop = null ;
		this.architecture = architecture ;
		this.createSimulator() ;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SupervisorNotificationI#acceptSimulationReport(fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI)
	 */
	@Override
	public void			acceptSimulationReport(SimulationReportI report)
	{
		// memorise the notified report.
		this.report = report ;
		this.logMessage("" + report) ;
	}
}
// -----------------------------------------------------------------------------
