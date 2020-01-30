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

import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;

// -----------------------------------------------------------------------------
/**
 * The class <code>ComponentAtomicModelDescriptor</code> implements a descriptor
 * of a DEVS atomic model that is held by a BCM component in a BCM component
 * assembly; using the DEVS closure over composition property, the model
 * actually created by the BCM component can be a coupled model but it will be
 * described as an atomic one in the global architecture for the
 * interconnection.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * When integrating DEVS simulation models with BCM components, a simulation
 * architecture (DEVS models composition) is mapped onto a component assembly.
 * Cyber-physical components have simulation models implementing their
 * behavioural model. These models can be obtained by composing submodels
 * within a coupled model at the level of the component that will be seen
 * as an atomic model from the outside. This is described by the local (to
 * each component) simulation architecture. At the component assembly level,
 * a global simulation architecture describes the way component simulation
 * models are composed to obtain a system-wide simulation architecture. The
 * present class is used in global architectures to describe component
 * simulation models.
 * </p>
 * <p>
 * Because cyber-physical components are required to create their own
 * simulation models from their local architecture and the atomic simulation
 * plug-in that is used to factor the code required to attach atomic simulation
 * models to BCM components, this descriptor is passive, holding only
 * information used by the parent coupled model in the process of
 * interconnecting models to create the global simulation architecture over
 * a component assembly.
 * </p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true	// TODO
 * </pre>
 * 
 * <p>Created on : 2019-06-17</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			ComponentAtomicModelDescriptor
extends		AbstractAtomicModelDescriptor
implements	ComponentModelDescriptorI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L ;
	/** URI of the reflection inbound port of the BCM component that is
	 *  holding the described model. 										*/
	protected final String		componentReflectionInboundPortURI ;

	// -------------------------------------------------------------------------
	// Constructors and creation static methods
	// -------------------------------------------------------------------------

	/**
	 * create a component atomic model descriptor.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	modelURI != null
	 * pre	importedEvents != null
	 * pre	exportedEvents != null
	 * pre	simulatedTimeUnit != null
	 * pre	compReflIBPURI != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param modelURI			URI of the model to be created.
	 * @param importedEvents	events imported by the model.
	 * @param exportedEvents	events exported by the model.
	 * @param simulatedTimeUnit	time unit used for the simulation clock.
	 * @param compReflIBPURI	URI of the reflection inbound port of the component holding the model.
	 */
	protected			ComponentAtomicModelDescriptor(
		String modelURI,
		Class<? extends EventI>[] importedEvents,
		Class<? extends EventI>[] exportedEvents,
		TimeUnit simulatedTimeUnit,
		String compReflIBPURI
		)
	{
		// neither the model class nor a factory needs to be given because
		// atomic models are created by the cyber-physical components holding
		// the model.
		super(null, modelURI, importedEvents, exportedEvents,
			  simulatedTimeUnit, null, null) ;

		assert	compReflIBPURI != null ;

		this.componentReflectionInboundPortURI = compReflIBPURI ;
	}

	/**
	 * check the invariant on the part of the descriptor implemented in this
	 * class (a similar method in the superclass addresses the invariant at
	 * this level).
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	d != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param d	descriptor to be checked.
	 * @return	true if the descriptor is conform to its invariant.
	 */
	public static boolean	checkInvariant(ComponentAtomicModelDescriptor d)
	{
		assert	d != null ;

		return d.componentReflectionInboundPortURI != null ;
	}

	/**
	 * create a component atomic model descriptor.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	modelURI != null
	 * pre	simulatedTimeUnit != null
	 * pre	compReflIBPURI != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param modelURI			URI of the model to be created.
	 * @param importedEvents	events imported by the model.
	 * @param exportedEvents	events exported by the model.
	 * @param simulatedTimeUnit	time unit used for the simulation clock.
	 * @param compReflIBPURI	URI of the reflection inbound port of the component holding the model.
	 * @return					the new model descriptor.
	 * @throws Exception		<i>to do.</i>
	 */
	@SuppressWarnings("unchecked")
	public static ComponentAtomicModelDescriptor	create(
		String modelURI,
		Class<? extends EventI>[] importedEvents,
		Class<? extends EventI>[] exportedEvents,
		TimeUnit simulatedTimeUnit,
		String compReflIBPURI
		) throws Exception
	{
		assert	modelURI != null ;
		assert	compReflIBPURI != null :
					new Exception(modelURI + " has no associated component!") ;

		if (importedEvents == null) {
			importedEvents = new Class[]{} ;
		}
		if (exportedEvents == null) {
			exportedEvents = new Class[]{} ;
		}
		return new ComponentAtomicModelDescriptor(
								modelURI, importedEvents, exportedEvents,
								simulatedTimeUnit, compReflIBPURI) ;
	}

	// -------------------------------------------------------------------------
	// Instance methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelDescriptorI#getComponentReflectionInboundPortURI()
	 */
	@Override
	public String		getComponentReflectionInboundPortURI()
	{
		return this.componentReflectionInboundPortURI ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor#connectAtomicModel()
	 */
	@Override
	public ModelDescriptionI	connectAtomicModel() throws Exception
	{
		throw new Exception("The method connectAtomicModel must not be "
							+ "called on instances of "
							+ "ComponentAtomicModelDescriptor.") ;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelDescriptorI#compose(fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelArchitectureI, java.lang.String)
	 */
	@Override
	public ModelDescriptionI	compose(
		ComponentModelArchitectureI architecture,
		String pnipURI
		)
	{
		throw new RuntimeException(
					"ComponentAtomicModelDescriptor::compose does not need to"
					+ "be called.") ;
	}
}
// -----------------------------------------------------------------------------
