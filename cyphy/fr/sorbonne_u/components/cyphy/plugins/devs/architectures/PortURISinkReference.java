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

import fr.sorbonne_u.devs_simulation.models.events.AbstractAtomicSinkReference;

//-----------------------------------------------------------------------------
/**
 * The class <code>PortURISinkReference</code> defines a reference to a
 * sink model that must receive external events that will require a component
 * connection to be put in place.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * In the DEVS simulation library used to integrate DEVS simulation models with
 * BCM components, models have to exchange external events directly with the
 * receiving model. The library is implementing that with a plain Java
 * reference, but when the two models are held by two distinct components, the
 * connection must use BCM component interfaces, ports and connectors. This
 * class allows to describe the reference with the URI of the other model
 * inbound port, thus the sending model component can connect using the
 * corresponding outbound port.
 * </p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2018-06-15</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			PortURISinkReference
extends		AbstractAtomicSinkReference
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L;
	/** URI of the events exchanging inbound port of the component holding
	 *  the sink model.														*/
	public final String			portURI ;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a new instance.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	portURI != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param portURI	URI of the events exchanging inbound port of the component holding the sink model.
	 */
	public				PortURISinkReference(String portURI)
	{
		super() ;

		assert	 portURI != null ;

		this.portURI = portURI ;
	}

	// -------------------------------------------------------------------------
	// Instance methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.AbstractAtomicSinkReference#isDirect()
	 */
	@Override
	public boolean		isDirect()
	{
		return false ;
	}
}
//-----------------------------------------------------------------------------
