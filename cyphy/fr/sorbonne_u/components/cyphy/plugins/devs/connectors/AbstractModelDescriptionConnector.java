package fr.sorbonne_u.components.cyphy.plugins.devs.connectors;

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

import java.util.Set;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelDescriptionCI;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.StaticVariableDescriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.interfaces.EventsExchangingI;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import fr.sorbonne_u.devs_simulation.interfaces.ParentNotificationI;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.ParentReferenceI;
import fr.sorbonne_u.devs_simulation.models.events.CallableEventAtomicSink;
import fr.sorbonne_u.devs_simulation.models.events.EventAtomicSource;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;

// -----------------------------------------------------------------------------
/**
 * The class <code>AbstractModelDescriptionConnector</code> implements the
 * connector for the offered/required interface <code>ModelDescriptionCI</code>.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 2018-06-01</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public abstract class	AbstractModelDescriptionConnector
extends		AbstractConnector
implements	ModelDescriptionCI
{
	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getURI()
	 */
	@Override
	public String		getURI() throws Exception
	{
		return ((ModelDescriptionI)this.offering).getURI() ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getSimulatedTimeUnit()
	 */
	@Override
	public TimeUnit		getSimulatedTimeUnit() throws Exception
	{
		return ((ModelDescriptionI)this.offering).getSimulatedTimeUnit() ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getParentURI()
	 */
	@Override
	public String		getParentURI() throws Exception
	{
		return ((ModelDescriptionI)this.offering).getParentURI() ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#isParentSet()
	 */
	@Override
	public boolean		isParentSet() throws Exception
	{
		return ((ModelDescriptionI)this.offering).isParentSet() ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#setParent(fr.sorbonne_u.devs_simulation.models.ParentReferenceI)
	 */
	@Override
	public void			setParent(ParentReferenceI p) throws Exception
	{
		((ModelDescriptionI)this.offering).setParent(p) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getParent()
	 */
	@Override
	public ParentNotificationI	getParent() throws Exception
	{
		return ((ModelDescriptionI)this.offering).getParent() ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getDescendentModel(java.lang.String)
	 */
	@Override
	public ModelI		getDescendentModel(String uri) throws Exception
	{
		throw new Exception("The method getDescendentModel should not be" +
							" called through a plug-in as it may cause " +
							" a reference leak to a non-RMI Java object.") ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#isRoot()
	 */
	@Override
	public boolean		isRoot() throws Exception
	{
		return ((ModelDescriptionI)this.offering).isRoot() ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#closed()
	 */
	@Override
	public boolean		closed() throws Exception
	{
		return ((ModelDescriptionI)this.offering).closed() ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getImportedEventTypes()
	 */
	@Override
	public Class<? extends EventI>[]		getImportedEventTypes()
	throws Exception
	{
		return ((ModelDescriptionI)this.offering).getImportedEventTypes() ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#isImportedEventType(java.lang.Class)
	 */
	@Override
	public boolean		isImportedEventType(Class<? extends EventI> ec)
	throws Exception
	{
		return ((ModelDescriptionI)this.offering).isImportedEventType(ec) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getExportedEventTypes()
	 */
	@Override
	public Class<? extends EventI>[]		getExportedEventTypes()
	throws Exception
	{
		return ((ModelDescriptionI)this.offering).getExportedEventTypes() ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#isExportedEventType(java.lang.Class)
	 */
	@Override
	public boolean		isExportedEventType(Class<? extends EventI> ec)
	throws Exception
	{
		return ((ModelDescriptionI)this.offering).isExportedEventType(ec) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#isDescendentModel(java.lang.String)
	 */
	@Override
	public boolean		isDescendentModel(String uri) throws Exception
	{
		return ((ModelDescriptionI)this.offering).isDescendentModel(uri) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getEventExchangingDescendentModel(java.lang.String)
	 */
	@Override
	public EventsExchangingI	getEventExchangingDescendentModel(String uri)
	throws Exception
	{
		return ((ModelDescriptionI)this.offering).getEventExchangingDescendentModel(uri) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getEventAtomicSource(java.lang.Class)
	 */
	@Override
	public EventAtomicSource		getEventAtomicSource(
		Class<? extends EventI> ce
		) throws Exception
	{
		return ((ModelDescriptionI)this.offering).getEventAtomicSource(ce) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getEventAtomicSinks(java.lang.Class)
	 */
	@Override
	public Set<CallableEventAtomicSink>		getEventAtomicSinks(
		Class<? extends EventI> ce
		) throws Exception
	{
		return ((ModelDescriptionI)this.offering).getEventAtomicSinks(ce) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#addInfluencees(java.lang.String, java.lang.Class, java.util.Set)
	 */
	@Override
	public void			addInfluencees(
		String modelURI,
		Class<? extends EventI> ce,
		Set<CallableEventAtomicSink> influencees
		) throws Exception
	{
		((ModelDescriptionI)this.offering).
								addInfluencees(modelURI, ce, influencees) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getInfluencees(java.lang.String, java.lang.Class)
	 */
	@Override
	public Set<CallableEventAtomicSink>		getInfluencees(
		String modelURI,
		Class<? extends EventI> ce
		) throws Exception
	{
		return ((ModelDescriptionI)this.offering).getInfluencees(modelURI, ce) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#areInfluencedThrough(java.lang.String, java.util.Set, java.lang.Class)
	 */
	@Override
	public boolean		areInfluencedThrough(
		String modelURI,
		Set<String> destinationModelURIs,
		Class<? extends EventI> ce
		) throws Exception
	{
		return ((ModelDescriptionI)this.offering).
					areInfluencedThrough(modelURI, destinationModelURIs, ce) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#isInfluencedThrough(java.lang.String, java.lang.String, java.lang.Class)
	 */
	@Override
	public boolean		isInfluencedThrough(
		String modelURI,
		String destinationModelURI,
		Class<? extends EventI> ce
		) throws Exception
	{
		return ((ModelDescriptionI)this.offering).
						isInfluencedThrough(modelURI, destinationModelURI, ce) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#isTIOA()
	 */
	@Override
	public boolean		isTIOA() throws Exception
	{
		return ((ModelDescriptionI)this.offering).isTIOA() ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#isOrdered()
	 */
	@Override
	public boolean		isOrdered() throws Exception
	{
		return ((ModelDescriptionI)this.offering).isOrdered() ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#isExportedVariable(java.lang.String, java.lang.Class)
	 */
	@Override
	public boolean		isExportedVariable(String name, Class<?> type)
	throws Exception
	{
		return ((ModelDescriptionI)this.offering).
											isExportedVariable(name, type) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#isImportedVariable(java.lang.String, java.lang.Class)
	 */
	@Override
	public boolean		isImportedVariable(String name, Class<?> type)
	throws Exception
	{
		return ((ModelDescriptionI)this.offering).
											isImportedVariable(name, type) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getImportedVariables()
	 */
	@Override
	public StaticVariableDescriptor[]	getImportedVariables()
	throws Exception
	{
		return ((ModelDescriptionI)this.offering).getImportedVariables() ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getExportedVariables()
	 */
	@Override
	public StaticVariableDescriptor[]	getExportedVariables()
	throws Exception
	{
		return ((ModelDescriptionI)this.offering).getExportedVariables() ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getActualExportedVariableValueReference(java.lang.String, java.lang.String, java.lang.Class)
	 */
	@Override
	public Value<?>		getActualExportedVariableValueReference(
		String modelURI,
		String sourceVariableName,
		Class<?> sourceVariableType
		) throws Exception
	{
		return ((ModelDescriptionI)this.offering).
					getActualExportedVariableValueReference(
						modelURI, sourceVariableName, sourceVariableType) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#setImportedVariableValueReference(java.lang.String, java.lang.String, java.lang.Class, fr.sorbonne_u.devs_simulation.hioa.models.vars.Value)
	 */
	@Override
	public void			setImportedVariableValueReference(
		String modelURI,
		String sinkVariableName,
		Class<?> sinkVariableType,
		Value<?> value
		) throws Exception
	{
		((ModelDescriptionI)this.offering).
				setImportedVariableValueReference(
						modelURI, sinkVariableName, sinkVariableType, value) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#setDebugLevel(int)
	 */
	@Override
	public void			setDebugLevel(int newDebugLevel) throws Exception
	{
		((ModelDescriptionI)this.offering).setDebugLevel(newDebugLevel) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getFinalReport()
	 */
	@Override
	public SimulationReportI		getFinalReport() throws Exception
	{
		return ((ModelDescriptionI)this.offering).getFinalReport() ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#modelAsString(java.lang.String)
	 */
	@Override
	public String		modelAsString(String indent) throws Exception
	{
		return ((ModelDescriptionI)this.offering).modelAsString(indent) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#simulatorAsString()
	 */
	@Override
	public String		simulatorAsString() throws Exception
	{
		return ((ModelDescriptionI)this.offering).simulatorAsString() ;
	}
}
// -----------------------------------------------------------------------------
