package app.components;

import java.util.concurrent.TimeUnit;

import app.interfaces.appareil.IConsommation;
import app.interfaces.appareil.IFrigo;
import app.interfaces.controleur.ICompteur;
import app.ports.compteur.CompteurOutPort;
import app.ports.frigo.FrigoConsoInPort;
import app.ports.frigo.FrigoInPort;
import app.util.EtatAppareil;
import app.util.ModeFrigo;
import app.util.TypeAppareil;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.ports.PortI;

@OfferedInterfaces(offered = { IFrigo.class, IConsommation.class })
@RequiredInterfaces(required = { ICompteur.class })
public class Frigo extends AbstractComponent implements IFrigo, IConsommation {

	/** port entrant permettant d'offrir les services du composant */
	protected FrigoInPort service_INPORT;
	
	/** port entrant permettant d'offrir l'acces a la consommation de l'appareil */
	protected FrigoConsoInPort consommation_INPORT;
	
	/** port sortant permettant a l'appareil de s'inscrire sur la liste des appareil du compteur */
	protected CompteurOutPort compteur_OUTPORT;

	protected TypeAppareil type;
	protected EtatAppareil etat;
	protected ModeFrigo lumiere_refrigerateur;
	protected ModeFrigo lumiere_congelateur;

	protected Double congelateur_temperature_cible;
	protected Double refrigerateur_temperature_cible;
	protected Double consommation;

	protected Frigo(String reflectionInboundPortURI, 
			int nbThreads, int nbSchedulableThreads, 
			String dataOutPortURI,
			TypeAppareil type) throws Exception {
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);

		service_INPORT = new FrigoInPort(dataOutPortURI, this);
		consommation_INPORT = new FrigoConsoInPort(dataOutPortURI, this);
		compteur_OUTPORT = new CompteurOutPort(dataOutPortURI, this);
		
		this.addPort(service_INPORT);
		this.addPort(consommation_INPORT);
		this.addPort(compteur_OUTPORT);
		
		service_INPORT.publishPort();
		consommation_INPORT.publishPort();
		compteur_OUTPORT.publishPort();
		
		this.tracer.setTitle("Frigo");
		this.tracer.setRelativePosition(0, 1);

		etat = EtatAppareil.ON;
		refrigerateur_temperature_cible = 3.0;
		congelateur_temperature_cible = -10.0;
		consommation = 55.0;
		this.type = type;
	}

	@Override
	public void setEtatAppareil(EtatAppareil etat) throws Exception {
		this.etat = etat;
	}

	@Override
	public double getConsommation() throws Exception {
		return consommation;
	}

	@Override
	public void setTemperature_Refrigerateur(double temperature) throws Exception {
		this.refrigerateur_temperature_cible = temperature;
	}

	@Override
	public void setTemperature_Congelateur(double temperature) throws Exception {
		this.congelateur_temperature_cible = temperature;
	}

	@Override
	public void setLumiere_Refrigerateur(ModeFrigo mf) throws Exception {
		this.lumiere_refrigerateur = mf;
	}

	@Override
	public void setLumiere_Congelateur(ModeFrigo mf) throws Exception {
		this.lumiere_congelateur = mf;
	}

	/**
	 * Permet de s'inscrire sur la liste des appareils du compteur
	 * @param uri
	 * @throws Exception
	 */
	protected void ajouterAppareil() throws Exception {
		this.compteur_OUTPORT.ajouterAppareil("FAIRE CONSTANTE");
		/** TODO **/
	}

	/**
	 * Adapatation de la temperature en fonction de la temperature cible
	 */
	protected void adaptationTemperature() {
		this.logMessage("regulation de la temperature...");
		/** TODO **/
	}

	@Override
	public void start() throws ComponentStartException {
		super.start();
		
		this.logMessage("Demarrage du frigo...");

		this.scheduleTask(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try { ((Frigo) this.getTaskOwner()).ajouterAppareil(); }
				catch (Exception e) { throw new RuntimeException(e); }
			}
		}, 1000, TimeUnit.MILLISECONDS);
	}

	@Override
	public void execute() throws Exception {
		super.execute();
		
		this.logMessage("Phase d'execution du frigo.");
		
		this.scheduleTaskWithFixedDelay(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try { ((Frigo) this.getTaskOwner()).adaptationTemperature(); } 
				catch (Exception e) { throw new RuntimeException(e); }
			}
		}, 2000, 1000, TimeUnit.MILLISECONDS);
	}
	
	@Override
	public void finalise() throws Exception {
		this.logMessage("Arret du composant frigo...") ;
		super.finalise();
	}
	
	@Override
	public void	shutdown() throws ComponentShutdownException
	{
		try {
			PortI[] port_service = this.findPortsFromInterface(IFrigo.class);
			PortI[] port_consommation = this.findPortsFromInterface(IConsommation.class);
			PortI[] port_compteur = this.findPortsFromInterface(ICompteur.class);
			
			port_service[0].unpublishPort() ;
			port_consommation[0].unpublishPort();
			port_compteur[0].unpublishPort();
		} catch (Exception e) { throw new ComponentShutdownException(e); }
		super.shutdown();
	}

	@Override
	public void shutdownNow() throws ComponentShutdownException
	{
		try {
			PortI[] port_service = this.findPortsFromInterface(IFrigo.class);
			PortI[] port_consommation = this.findPortsFromInterface(IConsommation.class);
			PortI[] port_compteur = this.findPortsFromInterface(ICompteur.class);
			
			port_service[0].unpublishPort() ;
			port_consommation[0].unpublishPort();
			port_compteur[0].unpublishPort();
		} catch (Exception e) { throw new ComponentShutdownException(e); }
		super.shutdownNow();
	}
}
