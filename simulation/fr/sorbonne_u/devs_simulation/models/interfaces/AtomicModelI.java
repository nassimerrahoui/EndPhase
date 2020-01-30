package fr.sorbonne_u.devs_simulation.models.interfaces ;

import java.util.ArrayList;

import fr.sorbonne_u.devs_simulation.interfaces.EventsExchangingI;
import fr.sorbonne_u.devs_simulation.interfaces.VariablesSharingI;
import fr.sorbonne_u.devs_simulation.models.events.EventI;

// -----------------------------------------------------------------------------
/**
 * The interface <code>AtomicModelI</code> defines the most generic methods to
 * be implemented by DEVS atomic simulation models.
 *
 * <p><strong>Description</strong></p>
 * 
 * 
 * <p>Created on : 2016-01-15</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface		AtomicModelI
extends		ModelI,
			EventsExchangingI,
			VariablesSharingI
{
	/**
	 * maps the current internal state to the output set; this method is
	 * user-model-dependent hence must be implemented by the user for atomic
	 * models.
	 * 
	 * <p>Description</p>
	 * 
	 * <p>
	 * Beware that when this method is called, though the simulation time
	 * has conceptually reached the time if the next internal event, the
	 * value returned by <code>getCurrentStateTime</code> has not yet been
	 * update to that time. Hence the actual current simulation time is
	 * given by
	 * <code>this.getCurrentStateTime().add(this.getNextTimeAdvance())</code>.
	 * </p>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.isInitialised()
	 * pre	this.getNextTimeAdvance().lessThan(Duration.INFINITY)
	 * pre	this.getNextTimeAdvance().equals(
	 * 				this.getTimeOfNextEvent().subtract(
	 * 									this.getCurrentStateTime()))
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	the corresponding external events or null if none.
	 */
	public ArrayList<EventI>	output() ;

	/**
	 * return the vector of all external events received during the last
	 * internal simulation step through <code>storeInput</code>, clearing
	 * them up to reinitialise the vector for the next step.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	the vector of all external events received during the last internal simulation step.
	 */
	public ArrayList<EventI>	getStoredEventAndReset() ;
}
// -----------------------------------------------------------------------------
