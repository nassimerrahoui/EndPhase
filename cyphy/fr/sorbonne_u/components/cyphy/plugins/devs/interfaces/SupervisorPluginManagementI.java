package fr.sorbonne_u.components.cyphy.plugins.devs.interfaces;

import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelArchitectureI;

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

// -----------------------------------------------------------------------------
/**
 * The interface <code>SupervisorPluginManagementI</code> declares the methods
 * offered by the supervisor plug-in to the components holding them to create
 * and connect simulation architectures.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2018-06-20</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface			SupervisorPluginManagementI
{
	/**
	 * connect this supervisor to the component C holding the root model in the
	 * simulation architecture it supervises and memorise the simulation
	 * plug-in management outbound port used to perform the connection and
	 * make C connect to the supervisor notification inbound port of this
	 * supervisor; this method is called by <code>createSimulator</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @throws Exception				<i>TO DO</i>.
	 */
	public void			connectRootSimulatorComponent()
	throws Exception ;

	/**
	 * create the simulation architecture given the description already
	 * set in the plug-in.
	 * 
	 * <p><strong>Description</strong></p>
	 * 
	 * Main entry point to install a simulation architecture from the supervisor
	 * component. The architecture description must have been set priori to the
	 * call. The method will first connect the components participating in the
	 * simulation architecture (using the method
	 * <code>connectRootSimulatorComponent</code> and then will compose their
	 * simulation models using the method <code>compose</code> defined by the
	 * architecture descriptor.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void			createSimulator() throws Exception ;

	/**
	 * set a new component-based simulation architecture to be instantiated and
	 * supervised.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	architecture != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param architecture	the new simulation architecture to be instantiated and supervised.
	 * @throws Exception	<i>todo</i>.
	 */
	public void			resetArchitecture(
		ComponentModelArchitectureI architecture
		) throws Exception ;
}
// -----------------------------------------------------------------------------
