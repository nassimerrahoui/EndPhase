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

import fr.sorbonne_u.devs_simulation.interfaces.ParentNotificationI;
import fr.sorbonne_u.devs_simulation.models.ParentReferenceI;
import fr.sorbonne_u.devs_simulation.models.architectures.ModelComposer;

// -----------------------------------------------------------------------------
/**
 * The class <code>ComponentModelComposer</code> implements methods used when
 * composing simulation models deployed on BCM components.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2019-06-27</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			ComponentModelComposer
extends		ModelComposer
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L ;
	/** URI of the parent notification inbound port of the component holding
	 *  the parent model.													*/
	protected String			parentNotificationInboundPortURI ;

	// -------------------------------------------------------------------------
	// Instance methods
	// -------------------------------------------------------------------------

	/**
	 * return true if the parent notification inbound port URI is set.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	true if the parent notification inbound port URI is set.
	 */
	public boolean		parentNotificationInboundPortURISet()
	{
		return this.parentNotificationInboundPortURI != null ;
	}

	/**
	 * set the parent notification inbound port URI.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	parentNotificationInboundPortURI != null
	 * post	ret != null
	 * </pre>
	 *
	 * @param parentNotificationInboundPortURI the parent notification inbound port URI to set
	 */
	public void			setParentNotificationInboundPortURI(
		String parentNotificationInboundPortURI
		)
	{
		assert	parentNotificationInboundPortURI != null ;

		this.parentNotificationInboundPortURI =
											parentNotificationInboundPortURI ;
	}

	/**
	 * return the component parent reference with the URI of the parent
	 * notification inbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * Parent references abstract away the actual implementation of the parent,
	 * as a simulation model, as a simulation engine or as a component holding
	 * a model and its simulation engine within a simulation plug-in.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.parentNotificationInboundPortURISet()
	 * post	ret != null
	 * </pre>
	 *
	 * @param parent	make a component parent reference from the reference to this model as parent.
	 * @return			the component parent reference with the URI of the parent notification inbound port.
	 */
	@Override
	protected ParentReferenceI	makeParentReference(ParentNotificationI parent)
	{
		assert	this.parentNotificationInboundPortURISet() ;

		return new ComponentParentReference(
									this.parentNotificationInboundPortURI) ;
	}
}
// -----------------------------------------------------------------------------
