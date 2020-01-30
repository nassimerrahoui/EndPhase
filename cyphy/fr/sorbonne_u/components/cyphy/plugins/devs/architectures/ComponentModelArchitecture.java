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

import java.util.Map;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cyphy.plugins.devs.ports.SimulatorPluginManagementOutboundPort;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.components.cyphy.plugins.devs.AbstractSimulatorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelArchitectureHelper ;

// -----------------------------------------------------------------------------
/**
 * The class <code>ComponentModelArchitecture</code> extends DEVS simulation
 * architectures to include information about the mapping of simulation models
 * to BCM components and provides methods used by the supervisor component to
 * create the system-wide simulator from this architecture onto the component
 * assemnly.
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
public class			ComponentModelArchitecture
extends		Architecture
implements	ComponentModelArchitectureI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L ;
	/** URI of the simulation architecture described by the instance.		*/
	protected final String		architectureURI ;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a component model architecture.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	architectureURI != null
	 * pre	rootModelURI != null
	 * pre	atomicModelDescriptors != null
	 * pre	coupledModelDescriptors != null
	 * pre	simulationTimeUnit != null
	 * pre	{@code atomicModelDescriptors.containsKey(rootModelURI) ||
	 * 				coupledModelDescriptors.containsKey(rootModelURI)}
	 * pre	forall md in atomicModelDescriptors.values() {
	 * 			md instanceof ComponentAtomicModelDescriptor
	 * 		}
	 * pre	forall md in coupledModelDescriptors.values() {
	 * 			md instanceof ComponentCoupledModelDescriptor
	 * 		}
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param architectureURI			URI of the architecture.
	 * @param rootModelURI				URI of the root model in the architecture.
	 * @param atomicModelDescriptors	map from atomic model URIs to their atomic model descriptors.
	 * @param coupledModelDescriptors	map from coupled model URIs to their coupled model descriptors.
	 * @param simulationTimeUnit		time unit for the simulation clocks.
	 * @throws Exception				<i>to do</i>.
	 */
	public				ComponentModelArchitecture(
		String architectureURI,
		String rootModelURI,
		Map<String, AbstractAtomicModelDescriptor> atomicModelDescriptors,
		Map<String, CoupledModelDescriptor> coupledModelDescriptors,
		TimeUnit simulationTimeUnit
		) throws Exception
	{
		super(rootModelURI, atomicModelDescriptors,
			  coupledModelDescriptors, simulationTimeUnit) ;

		// Preconditions
		assert	architectureURI != null ;
		assert	atomicModelDescriptors.containsKey(rootModelURI) ||
						coupledModelDescriptors.containsKey(rootModelURI) ;
		for (AbstractAtomicModelDescriptor md :
											atomicModelDescriptors.values()) {
			assert	md instanceof ComponentAtomicModelDescriptor ;
		}
		for (CoupledModelDescriptor md : coupledModelDescriptors.values()) {
			assert	md instanceof ComponentCoupledModelDescriptor ;
		}

		this.architectureURI = architectureURI ;
	}

	/**
	 * check the invariant related to the present class only (a similar method
	 * is defiend on the superclass).
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param ma	component model architecture to be checked.
	 * @return		true if the invariant is satisfied.
	 */
	public static boolean	checkCompleteArchitectureInvariant(
		ComponentModelArchitecture ma
		)
	{
		boolean invariant = true ;

		// TODO
//		for (Entry<String,String> e : ma.modelsParent.entrySet()) {
//			String modelURI = e.getKey() ;
//			System.out.println(
//				"ComponentBasedModelArchitecture#"
//								+ "checkCompleteArchitectureInvariant "
//				+ modelURI + " " + ma.modelURIs2componentURIs) ;
//			String childComponentURI =
//							ma.modelURIs2componentURIs.get(modelURI) ;
//			String parentComponentURI =
//							ma.modelURIs2componentURIs.get(e.getValue()) ;
//			if (!childComponentURI.equals(parentComponentURI)) {
//				invariant &=
//					!ma.isEngineCreationMode(
//								modelURI,
//								SimulationEngineCreationMode.NO_ENGINE) ;
//			}
//		}

		return invariant ;
	}

	// -------------------------------------------------------------------------
	// Instance methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelArchitectureI#getArchitectureURI()
	 */
	@Override
	public String		getArchitectureURI()
	{
		return this.architectureURI ;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelArchitectureI#getReflectionInboundPortURI(java.lang.String)
	 */
	@Override
	public String		getReflectionInboundPortURI(String modelURI)
	{
		assert	modelURI != null && this.isModel(modelURI) ;

		return this.getModelDescriptor(modelURI).
									getComponentReflectionInboundPortURI() ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.Architecture#getModelDescriptor(java.lang.String)
	 */
	@Override
	public ComponentModelDescriptorI	getModelDescriptor(String uri)
	{
		return (ComponentModelDescriptorI) super.getModelDescriptor(uri) ;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelArchitectureI#connectRootModelComponent(fr.sorbonne_u.components.AbstractComponent)
	 */
	@Override
	public SimulatorPluginManagementOutboundPort	connectRootModelComponent(
		AbstractComponent creator
		)
	{
		ComponentModelDescriptorI d =
							this.getModelDescriptor(this.getRootModelURI()) ;

		if (d instanceof CoupledModelDescriptor) {
			return ComponentModelArchitectureHelper.
							connectCoupledModelComponent(
									creator,
									d.getModelURI(),
									d.getComponentReflectionInboundPortURI()) ;
		} else {
			assert	d instanceof AtomicModelDescriptor ;
			return ComponentModelArchitectureHelper.
							connectAtomicModelComponent(
									creator,
									d.getModelURI(),
									d.getComponentReflectionInboundPortURI()) ;
		}
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelArchitectureI#compose(java.lang.String, fr.sorbonne_u.components.AbstractComponent, java.lang.String, fr.sorbonne_u.components.cyphy.plugins.devs.AbstractSimulatorPlugin)
	 */
	@Override
	public ModelDescriptionI	compose(
		String modelURI,
		AbstractComponent creator,
		String pnipURI,
		AbstractSimulatorPlugin plugin
		) throws Exception
	{
		assert	modelURI != null ;
		assert	plugin != null && plugin.getPluginURI().equals(modelURI) ;
		assert	creator != null && creator.isInstalled(modelURI) ;

		ComponentCoupledModelDescriptor descriptor =
				(ComponentCoupledModelDescriptor)
								this.coupledModelDescriptors.get(modelURI) ;
		descriptor.setCreatorComponent(creator, plugin) ;
		return descriptor.compose(this, pnipURI) ;
	}
}
// -----------------------------------------------------------------------------
