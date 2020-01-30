package fr.sorbonne_u.devs_simulation.interfaces;

import java.util.ArrayList;

import fr.sorbonne_u.devs_simulation.models.events.EventI;

// -----------------------------------------------------------------------------
/**
 * The interface <code>EventsExchangingI</code> declares the method used to
 * propagate events among models and simulation engines in a DEVS simulation.
 *
 * <p><strong>Description</strong></p>
 * 
 * The DEVS protocol proposes two ways to propagate events among models.
 * The first way is to return the produced events to the parent coordination
 * engine which passes them to a sibling or return them to its own parent
 * coordination engine.
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
public interface		EventsExchangingI
extends		ModelDescriptionI
{
	/**
	 * store external events imported from another simulation model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this instanceof AtomicModelI
	 * pre	destinationURI != null
	 * pre	this.getURI().equals(destinationURI)
	 * pre	{@code es != null && !es.isEmpty()}
	 * pre	for(EventI e : es) { this.isImportedEventType(e.getClass()) }
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param destinationURI		URI of the destination model.
	 * @param es					imported external events to be stored.
	 * @throws Exception			<i>TO DO</i>.
	 */
	public void			storeInput(String destinationURI, ArrayList<EventI> es)
	throws Exception ;
}
// -----------------------------------------------------------------------------
