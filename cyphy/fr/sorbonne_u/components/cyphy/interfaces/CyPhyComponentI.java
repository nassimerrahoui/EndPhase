package fr.sorbonne_u.components.cyphy.interfaces;

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

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentAtomicModelDescriptor;

//------------------------------------------------------------------------------
/**
 * The interface <code>CyPhyComponentI</code> declares the methods that must be
 * implemented by the cyber-physical component.
 *
 * <p><strong>Description</strong></p>
 * 
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2019-11-04</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface		CyPhyComponentI
extends		ComponentI
{
	/**
	 * return the URI of the simulation management inbound port added by the
	 * given plug-in.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param pluginURI		URI of the plug-in for which the URI of the management port is sought.
	 * @return				the URI of the simulation management inbound port added by the given plug-in.
	 * @throws Exception	<i>todo</i>.
	 */
	public String		getSimulatorPluginManagementInboundPortURI(
		String pluginURI
		) throws Exception ;

	/**
	 * return the URI of the simulator inbound port added by the given plug-in.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param pluginURI		URI of the plug-in for which the URI of the management port is sought.
	 * @return				the URI of the simulator inbound port added by the given plug-in.
	 * @throws Exception	<i>todo</i>.
	 */
	public String		getSimulatorInboundPortURI(
		String pluginURI
		) throws Exception ;

	/**
	 * get the component atomic model descriptor that describes the simulation
	 * model of this component as an atomic model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param modelURI		URI of the model which descriptor is sought.
	 * @return				the component atomic model descriptor that describes the simulation model with the given URI as an atomic model.
	 * @throws Exception	<i>todo</i>.
	 */
	public ComponentAtomicModelDescriptor	getAtomicModelDescriptor(
		String modelURI
		) throws Exception ;
}
//------------------------------------------------------------------------------
