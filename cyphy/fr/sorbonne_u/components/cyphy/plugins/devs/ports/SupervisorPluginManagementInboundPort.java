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

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelArchitectureI;
import fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SupervisorPluginManagementCI;

// -----------------------------------------------------------------------------
/**
 * The class <code>SupervisorPluginManagementInboundPort</code> implements the
 * inbound port for the offered interface
 * <code>SupervisorPluginManagementCI</code>.
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
public class			SupervisorPluginManagementInboundPort
extends		AbstractSimulationManagementInboundPort
implements	SupervisorPluginManagementCI
{
	private static final long serialVersionUID = 1L;

	public				SupervisorPluginManagementInboundPort(
		String uri,
		String pluginURI,
		ComponentI owner
		) throws Exception
	{
		super(uri, SupervisorPluginManagementCI.class, pluginURI, owner);
	}

	public				SupervisorPluginManagementInboundPort(
		String pluginURI,
		ComponentI owner
		) throws Exception
	{
		super(SupervisorPluginManagementCI.class, pluginURI, owner) ;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SupervisorPluginManagementI#connectRootSimulatorComponent()
	 */
	@Override
	public void			connectRootSimulatorComponent()
	throws Exception
	{
		this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Void>(this.pluginURI) {
					@Override
					public Void call() throws Exception {
						((SupervisorPluginManagementCI)
							this.getServiceProviderReference()).
								connectRootSimulatorComponent() ;
						return null;
					}
				}) ;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SupervisorPluginManagementI#createSimulator()
	 */
	@Override
	public void			createSimulator() throws Exception
	{
		this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Void>(this.pluginURI) {
					@Override
					public Void call() throws Exception {
						((SupervisorPluginManagementCI)
									this.getServiceProviderReference()).
														createSimulator() ;
						return null;
					}
				}) ;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SupervisorPluginManagementI#resetArchitecture(fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelArchitectureI)
	 */
	@Override
	public void			resetArchitecture(
		ComponentModelArchitectureI architecture
		) throws Exception
	{
		this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Void>(this.pluginURI) {
					@Override
					public Void call() throws Exception {
						((SupervisorPluginManagementCI)
									this.getServiceProviderReference()).
											resetArchitecture(architecture) ;
						return null;
					}
				}) ;
	}
}
// -----------------------------------------------------------------------------
