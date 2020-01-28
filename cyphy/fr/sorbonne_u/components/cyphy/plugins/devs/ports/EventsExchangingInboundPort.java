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

import java.util.ArrayList;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.EventsExchangingCI;
import fr.sorbonne_u.devs_simulation.interfaces.EventsExchangingI;
import fr.sorbonne_u.devs_simulation.models.events.EventI;

// -----------------------------------------------------------------------------
/**
 * The class <code>EventsExchangingInboundPort</code> implements the inbound
 * port for the offered interface <code>EventsExchangingCI</code>.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2018-06-01</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			EventsExchangingInboundPort
extends		AbstractModelDescriptionInboundPort
implements	EventsExchangingCI
{
	private static final long serialVersionUID = 1L;

	public				EventsExchangingInboundPort(
		String uri,
		String pluginURI,
		ComponentI owner
		) throws Exception
	{
		super(uri, EventsExchangingCI.class, pluginURI, owner);
	}

	public				EventsExchangingInboundPort(
		String pluginURI,
		ComponentI owner
		) throws Exception
	{
		super(EventsExchangingCI.class, pluginURI, owner);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.EventsExchangingI#storeInput(java.lang.String, ArrayList)
	 */
	@Override
	public void			storeInput(
		String destinationURI,
		ArrayList<EventI> es
		) throws Exception 
	{
		this.owner.handleRequestSync(
				new AbstractComponent.AbstractService<Void>(this.pluginURI) {
					@Override
					public Void call() throws Exception {
						((EventsExchangingI)
							this.getServiceProviderReference()).
											storeInput(destinationURI, es) ;
						return null;
					}
				}) ;
	}
}
// -----------------------------------------------------------------------------
