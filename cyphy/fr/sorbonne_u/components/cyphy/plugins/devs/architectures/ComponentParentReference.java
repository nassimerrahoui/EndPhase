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
import fr.sorbonne_u.components.cyphy.plugins.devs.connectors.ParentNotificationConnector;
import fr.sorbonne_u.components.cyphy.plugins.devs.ports.ParentNotificationOutboundPort;
import fr.sorbonne_u.devs_simulation.interfaces.ParentNotificationI;
import fr.sorbonne_u.devs_simulation.models.ParentReferenceI;

// -----------------------------------------------------------------------------
/**
 * The class <code>ComponentParentReference</code> defines a reference to a
 * parent (coupled) model that will require a component connection to be put
 * in place.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * In the DEVS simulation library used to integrate DEVS simulation models with
 * BCM components, models have to notify their parent coupled model when they
 * have received an external event in order for the corresponding coordinator
 * to pass them the control to execute their external transition. The library
 * is implementing that with a plain Java reference, but when the submodel and
 * its parent are held by two distinct components, the connection must use
 * BCM component interfaces, ports and connectors. This class allows to describe
 * the reference with the URI of the parent inbound port, thus the submodel
 * component can connect using the corresponding outbound port.
 * </p>
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
public class			ComponentParentReference
implements	ParentReferenceI
{
	private static final long serialVersionUID = 1L;
	protected final String	parentNotificationInboundPortURI ;
	transient protected ParentNotificationOutboundPort pnop ;

	/**
	 * create a new instance.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	parentNotificationInboundPortURI != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param parentNotificationInboundPortURI	URI of the parent notification inbound port of the component holding the parent coupled model.
	 */
	public				ComponentParentReference(
		String	parentNotificationInboundPortURI
		)
	{
		assert	parentNotificationInboundPortURI != null ;

		this.parentNotificationInboundPortURI =
										parentNotificationInboundPortURI ;
	}

	/**
	 * return true if the parent reference is set.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	true if the parent reference is set.
	 */
	public boolean		parentReferenceSet()
	{
		return this.pnop != null ;
	}

	/**
	 * set and connect the parent notification outbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code pnop != null && !pnop.connected()}
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param submodelHolder	component holding the submodel to be connected to its parent coupled model.
	 * @param pnop				outbound port requiring the parent notification component interface.
	 */
	public void			setComponentParentReference(
		AbstractComponent submodelHolder,
		ParentNotificationOutboundPort pnop
		)
	{
		try {
			assert	pnop != null && !pnop.connected() ;

			this.pnop = pnop ;
			submodelHolder.doPortConnection(
					pnop.getPortURI(),
					this.parentNotificationInboundPortURI,
					ParentNotificationConnector.class.getCanonicalName()) ;
		} catch (Exception e1) {
			throw new RuntimeException(e1) ;
		}
	}

	/**
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.parentReferenceSet()
	 * post	ret != null ;
	 * </pre>
	 * 
	 * @see fr.sorbonne_u.devs_simulation.models.ParentReferenceI#getParentReference()
	 */
	@Override
	public ParentNotificationI	getParentReference()
	{
		assert	this.parentReferenceSet() ;

		return this.pnop ;
	}
}
// -----------------------------------------------------------------------------
