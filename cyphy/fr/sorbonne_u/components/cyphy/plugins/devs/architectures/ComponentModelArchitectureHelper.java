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

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cyphy.connectors.CyPhyReflectionConnector;
import fr.sorbonne_u.components.cyphy.plugins.devs.CoordinatorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.connectors.SimulatorConnector;
import fr.sorbonne_u.components.cyphy.plugins.devs.connectors.SimulatorPluginManagementConnector;
import fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI;
import fr.sorbonne_u.components.cyphy.plugins.devs.ports.SimulatorOutboundPort;
import fr.sorbonne_u.components.cyphy.plugins.devs.ports.SimulatorPluginManagementOutboundPort;
import fr.sorbonne_u.components.cyphy.ports.CyPhyReflectionOutboundPort;

// -----------------------------------------------------------------------------
/**
 * The class <code>ComponentArchitectureHelper</code> defines code that is
 * used by component simulation architectures to create and interconnect
 * simulation models held by BCM components.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2019-06-24</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public abstract class	ComponentModelArchitectureHelper
{
	/**
	 * install the coordination plug-in on the component with the given
	 * reflection inbound port URI and then create, connect to it and return a
	 * simulator plug-in management outbound port attached to the component
	 * <code>creator</code> that is executing this method.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	creator != null
	 * pre	modelURI != null
	 * pre	componentReflectionInboundPort != null
	 * post	{@code ret != null && ret.connected()}
	 * </pre>
	 *
	 * @param creator							component executing this method.
	 * @param modelURI							URI of the model held by the component with the given reflection inbound port URI.
	 * @param componentReflectionInboundPort	URI of the reflection inbound port holding the model with URI <code>modelURI</code>.
	 * @return									the simulator plug-in management outbound port connected to the component with the given reflection inbound port URI.
	 */
	public static SimulatorPluginManagementOutboundPort
												connectCoupledModelComponent(
		AbstractComponent creator,
		String modelURI,
		String componentReflectionInboundPort
		)
	{
		assert	creator != null ;
		assert	modelURI != null ;
		assert	componentReflectionInboundPort != null ;

		SimulatorPluginManagementOutboundPort smop = null ;
		try {
			CyPhyReflectionOutboundPort rop =
									new CyPhyReflectionOutboundPort(creator) ;
			rop.publishPort() ;
			creator.doPortConnection(
						rop.getPortURI(),
						componentReflectionInboundPort,
						CyPhyReflectionConnector.class.getCanonicalName()) ;
			// TODO: parameterise this to create user-defined plug-ins.
			CoordinatorPlugin cp = new CoordinatorPlugin() ;
			cp.setPluginURI(modelURI) ;
			rop.installPlugin(cp) ;
			smop = ComponentModelArchitectureHelper.
								connectManagementPort(creator, modelURI, rop) ;
			creator.doPortDisconnection(rop.getPortURI()) ;
			rop.unpublishPort() ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}

		try {
			assert	smop.connected() ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
		return smop ;
	}

	/**
	 * create, connect and return the simulation management outbound port for
	 * the component <code>creator</code> (which executes this code); the
	 * returned port is connected with the simulation management inbound port
	 * of the component to which the reflection outbound port <code>rop</code>
	 * is itself connected.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param creator		creator component executing this code.
	 * @param modelURI		URI of the model held by the creator component.
	 * @param rop			reflection outbound port held by <code>creator</code>.
	 * @return				the simulation management outbound port held by the component <code>creator</code>
	 * @throws Exception	<i>todo</i>.
	 */
	protected static SimulatorPluginManagementOutboundPort
														connectManagementPort(
		AbstractComponent creator,
		String modelURI,
		CyPhyReflectionOutboundPort rop
		) throws Exception
	{
		SimulatorPluginManagementOutboundPort smop = null ;
		String smipURI =
				rop.getSimulatorPluginManagementInboundPortURI(modelURI) ;
		assert	smipURI != null ;
		smop = new SimulatorPluginManagementOutboundPort(creator) ;
		smop.publishPort() ;
		creator.doPortConnection(
				smop.getPortURI(),
				smipURI,
				SimulatorPluginManagementConnector.class.getCanonicalName()) ;
		return smop ;
	}

	/**
	 * create, connect to it and return a simulator plug-in management outbound
	 * port attached to the component <code>creator</code> that is executing
	 * this method; the returned port is connected to its corresponding inbound
	 * port in the component with the given reflection inbound port URI.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param creator							component executing this method.
	 * @param modelURI							URI of the model held by the creator component.
	 * @param componentReflectionInboundPort	URI of the reflection inbound port holding the model with URI <code>modelURI</code>.
	 * @return									the simulator plug-in management outbound port connected to the component with the given reflection inbound port URI.
	 */
	public static SimulatorPluginManagementOutboundPort
												connectAtomicModelComponent(
		AbstractComponent creator,
		String modelURI,
		String componentReflectionInboundPort
		)
	{
		SimulatorPluginManagementOutboundPort smop = null ;

		try {
			CyPhyReflectionOutboundPort rop =
									new CyPhyReflectionOutboundPort(creator) ;
			rop.publishPort() ;
			creator.doPortConnection(
						rop.getPortURI(),
						componentReflectionInboundPort,
						CyPhyReflectionConnector.class.getCanonicalName()) ;
			smop = ComponentModelArchitectureHelper.
								connectManagementPort(creator, modelURI, rop) ;
			creator.doPortDisconnection(rop.getPortURI()) ;
			rop.unpublishPort() ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}

		return smop ;
	}

	/**
	 * connect and return the outbound port connected to the model of the
	 * component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param creator							component performing the connection.
	 * @param modelURI							URI of the model to which the connection is made.
	 * @param componentReflectionInboundPort	URI of the component holding the model
	 * @return									the outbound port connected to the model of the component.
	 */
	protected static SimulatorOutboundPort	connectModelComponentAsSimulator(
		AbstractComponent creator,
		String modelURI,
		String componentReflectionInboundPort
		)
	{
		// System.out.println("ComponentArchitectureHelper#connectModelComponentAsSimulator 1") ;
		SimulatorOutboundPort sop = null ;

		try {
			CyPhyReflectionOutboundPort rop =
									new CyPhyReflectionOutboundPort(creator) ;
			rop.publishPort() ;
			creator.doPortConnection(
					rop.getPortURI(),
					componentReflectionInboundPort,
					CyPhyReflectionConnector.class.getCanonicalName()) ;
			String sipURI = rop.getSimulatorInboundPortURI(modelURI) ;
			assert	sipURI != null ;
			sop = new SimulatorOutboundPort(SimulatorCI.class, creator) ;
			sop.publishPort() ;
			creator.doPortConnection(
					sop.getPortURI(),
					sipURI,
					SimulatorConnector.class.getCanonicalName());
			creator.doPortDisconnection(rop.getPortURI()) ;
			rop.unpublishPort() ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}

		return sop ;
	}
}
// -----------------------------------------------------------------------------
