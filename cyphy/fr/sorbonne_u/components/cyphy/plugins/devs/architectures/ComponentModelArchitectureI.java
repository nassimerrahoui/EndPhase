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

import fr.sorbonne_u.devs_simulation.architectures.ArchitectureI;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cyphy.plugins.devs.AbstractSimulatorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.ports.SimulatorPluginManagementOutboundPort;

// -----------------------------------------------------------------------------
/**
 * The interface <code>ComponentBaseModelArchitectureI</code> declares the
 * methods that are specific to component-based simulation architectures.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2018-06-26</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface			ComponentModelArchitectureI
extends		ArchitectureI
{
	/**
	 * get the URI of the described architecture.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	ret != null
	 * </pre>
	 *
	 * @return	the URI of the described architecture.
	 */
	public String			getArchitectureURI() ;

	/**
	 * return the component model descriptor associated to the given
	 * model URI.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param uri	URI of a model in the architecture.
	 * @return		the component model descriptor of the given model.
	 */
	public ComponentModelDescriptorI	getModelDescriptor(String uri) ;

	/**
	 * return the URI of the reflection inbound port of the component holding
	 * the corresponding model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code modelURI != null && this.isModel(modelURI)}
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param modelURI	the URI of a model in this simulation architecture.
	 * @return			the URI of the reflection inbound port of the component holding the corresponding model.
	 */
	public String		getReflectionInboundPortURI(String modelURI) ;

	/**
	 * connect the component holding the root model of this architecture with
	 * the componnent <code>creator</code> (usually a supervisor component)
	 * through the returned simulation plug-in management outbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	creator != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param creator	component that will hold the returned simulation plug-in management outbound port.
	 * @return			the simulation plug-in management outbound port.
	 */
	public SimulatorPluginManagementOutboundPort	connectRootModelComponent(
		AbstractComponent creator
		) ;

	/**
	 * compose the simulation architecture subtree having the model with URI
	 * <code>modelURI</code> at its root by recursively composing the submodels
	 * and creating the required coupled model on the component
	 * <code>creator</code> that must hold it in the assembly, which has
	 * created the required plug-in <code>plugin</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	modelURI != null
	 * pre	creator != null
	 * pre	{@code plugin != null && plugin.getPluginURI().equals(modelURI)}
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param modelURI		URI of the coupled model to be composed.
	 * @param creator		component that executes the method and that holds the model.
	 * @param pnipURI		parent notification inbound port URI of the creator component.
	 * @param plugin		coordination plug-in of the <code>creator</code>.
	 * @return				the reference to the simulation engine executing the model.
	 * @throws Exception	<i>todo</i>.
	 */
	public ModelDescriptionI	compose(
		String modelURI,
		AbstractComponent creator,
		String pnipURI,
		AbstractSimulatorPlugin plugin
		) throws Exception ;
}
// -----------------------------------------------------------------------------
