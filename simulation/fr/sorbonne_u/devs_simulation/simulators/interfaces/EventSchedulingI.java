package fr.sorbonne_u.devs_simulation.simulators.interfaces;

// Copyright Jacques Malenfant, Sorbonne Universite.
//
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a
// new implementation of the DEVS simulation standard for Java.
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

import java.util.concurrent.ScheduledFuture;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;

//------------------------------------------------------------------------------
/**
 * The interface <code>EventSchedulingI</code> provides a common signature
 * to simulators for the scheduling of event processing tasks.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2019-12-17</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface		EventSchedulingI
{
	/**
	 * return true if the simulation time <code>t</code> corresponds with
	 * sufficient precision to the current real time as given by the system
	 * clock.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param t
	 * @return
	 * @throws Exception
	 */
	public boolean		isCurrentRealTime(Time t) throws Exception ;

	/**
	 * 
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	eventProcessingTask != null
	 * pre	{@code currentSimulationTime != null && isCurrentRealTime(currentSimulationTime)}
	 * pre	delay != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param eventProcessingTask	the event processing task to be scheduled.
	 * @param currentSimulationTime	current simulation time when performing this scheduling.
	 * @param delay					delay until the start of the task to be scheduled.
	 * @return						a scheduled future allowing to cancel the event processing task.
	 */
	public ScheduledFuture<Void>	scheduleEventProcessingTask(
		Runnable eventProcessingTask,
		Time currentSimulationTime,
		Duration delay
		) ;
}
//------------------------------------------------------------------------------
