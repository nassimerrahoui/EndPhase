package app.components;

import java.util.concurrent.ConcurrentHashMap;

import app.interfaces.assembleur.IComposantDynamique;
import app.interfaces.compteur.ICompteur;
import app.interfaces.compteur.ICompteurControleur;
import app.ports.compteur.CompteurAssembleurInPort;
import app.ports.compteur.CompteurInPort;
import app.ports.compteur.ConsommationProductionInPort;
import app.util.URI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.ports.PortI;

@OfferedInterfaces(offered = { ICompteurControleur.class, ICompteur.class, IComposantDynamique.class })
@RequiredInterfaces(required = { })
public class Compteur extends AbstractComponent {

	protected ConcurrentHashMap<String, Double> appareil_consommation = new ConcurrentHashMap<>();
	protected ConcurrentHashMap<String, Double> unite_production = new ConcurrentHashMap<>();

	protected Compteur(
			String COMPTEUR_URI,
			int nbThreads, int nbSchedulableThreads) throws Exception {
		super(COMPTEUR_URI, nbThreads, nbSchedulableThreads);
		
		// port entrant pour recuperer les consommation des appareils
		// et recuperer les productions des unites de production
		ConsommationProductionInPort consommation_production_INPORT = new ConsommationProductionInPort(this);
		
		// port entrant permettant au controleur de recuperer des informations depuis le compteur
		CompteurInPort action_INPORT = new CompteurInPort(this);
		
		// port entrant pour l'assembleur
		CompteurAssembleurInPort launch_port = new CompteurAssembleurInPort(this);
		
		consommation_production_INPORT.publishPort();
		action_INPORT.publishPort();
		launch_port.publishPort();
		
		if (AbstractCVM.isDistributed) {
			this.executionLog.setDirectory(System.getProperty("user.dir")) ;
		} else {
			this.executionLog.setDirectory(System.getProperty("user.home")) ;
		}
		
		this.createNewExecutorService(URI.POOL_CONSO_PROD_COMPTEUR_URI.getURI(), 5, false) ;
		this.createNewExecutorService(URI.POOL_CONTROLE_COMPTEUR_URI.getURI(), 5, false) ;
		
		// affichage
		this.tracer.setTitle("Compteur");
		this.tracer.setRelativePosition(1, 3);
		this.toggleTracing();
	}

	public void ajouterAppareil(String uri) throws Exception {
		this.appareil_consommation.put(uri, 0.0);
		this.logMessage(uri + " a ete ajoute au compteur");
		this.logMessage("...");
	}
	
	public void ajouterUniteProduction(String uri) throws Exception {
		this.unite_production.put(uri, 0.0);
		this.logMessage(uri + " a ete ajoute au compteur");
		this.logMessage("...");
	}

	public double envoyerConsommationGlobale() throws Exception {
		return appareil_consommation.values().stream().mapToDouble(i -> i).sum();
	}

	public double envoyerProductionGlobale() throws Exception {
		return unite_production.values().stream().mapToDouble(i -> i).sum();
	}
	
	public void setAppareilConsommation(String uri, double consommation) throws Exception {
		double c = (double) Math.round(consommation);
		if(appareil_consommation.containsKey(uri)) {
			appareil_consommation.put(uri, c);
			this.logMessage(uri + " consomme " + c + " Watt.");
			this.logMessage("...");
		}
	}

	public void setUniteProduction(String uri, double production) throws Exception {
		double p = (double) Math.round(production);
		if(unite_production.containsKey(uri)) {
			unite_production.put(uri, p);
			this.logMessage(uri + " produit " + p + " Watt.");
			this.logMessage("...");
		}
	}
	
	// ************* Cycle de vie du composant ************* 

	@Override
	public void start() throws ComponentStartException {
		super.start();
		this.logMessage("Demarrage du compteur...");
		this.logMessage("Phase d'execution du compteur.");
	}
	
	public void dynamicExecute() {
		/** TODO */
	}
	
	@Override
	public void finalise() throws Exception {
		this.logMessage("Arret du composant compteur...") ;
		super.finalise();
	}
	
	@Override
	public void	shutdown() throws ComponentShutdownException
	{
		try {
			PortI[] p1 = this.findPortsFromInterface(ICompteur.class);
			PortI[] p2 = this.findPortsFromInterface(ICompteurControleur.class);
			PortI[] p3 = this.findPortsFromInterface(IComposantDynamique.class);
			
			p1[0].unpublishPort();
			p2[0].unpublishPort();
			p3[0].unpublishPort();
		} catch (Exception e) { throw new ComponentShutdownException(e); }
		super.shutdown();
	}

	@Override
	public void shutdownNow() throws ComponentShutdownException
	{
		try {
			PortI[] p1 = this.findPortsFromInterface(ICompteur.class);
			PortI[] p2 = this.findPortsFromInterface(ICompteurControleur.class);
			PortI[] p3 = this.findPortsFromInterface(IComposantDynamique.class);
			
			p1[0].unpublishPort();
			p2[0].unpublishPort();
			p3[0].unpublishPort();
		} catch (Exception e) { throw new ComponentShutdownException(e); }
		super.shutdownNow();
	}
}