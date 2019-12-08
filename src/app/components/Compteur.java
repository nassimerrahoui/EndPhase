package app.components;

import java.util.concurrent.ConcurrentHashMap;
import app.interfaces.compteur.ICompteur;
import app.interfaces.compteur.ICompteurControleur;
import app.ports.compteur.CompteurInPort;
import app.ports.compteur.CompteurOutPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cvm.AbstractCVM;

@OfferedInterfaces(offered = { ICompteurControleur.class })
@RequiredInterfaces(required = { ICompteur.class })
public class Compteur extends AbstractComponent {

	protected ConcurrentHashMap<String, Double> appareil_consommation = new ConcurrentHashMap<>();
	protected ConcurrentHashMap<String, Double> unite_production = new ConcurrentHashMap<>();
	
	protected CompteurOutPort frigo_OUTPORT;
	protected CompteurOutPort lavelinge_OUTPORT;
	protected CompteurOutPort ordinateur_OUTPORT;
	protected CompteurOutPort batterie_OUTPORT;
	protected CompteurOutPort panneau_OUTPORT;

	public Compteur(String compteurURI, int nbThreads, int nbSchedulableThreads) throws Exception {
		super(compteurURI, nbThreads, nbSchedulableThreads);
		
		frigo_OUTPORT = new CompteurOutPort(this);
		lavelinge_OUTPORT = new CompteurOutPort(this);
		ordinateur_OUTPORT = new CompteurOutPort(this);
		batterie_OUTPORT = new CompteurOutPort(this);
		panneau_OUTPORT = new CompteurOutPort(this);
		
		// port entrant permettant au controleur de recuperer des informations
		// depuis le compteur
		CompteurInPort action_INPORT = new CompteurInPort(this);
		
		this.addPort(frigo_OUTPORT);
		this.addPort(lavelinge_OUTPORT);
		this.addPort(ordinateur_OUTPORT);
		this.addPort(batterie_OUTPORT);
		this.addPort(panneau_OUTPORT);
		this.addPort(action_INPORT);
		
		frigo_OUTPORT.publishPort();
		lavelinge_OUTPORT.publishPort();
		ordinateur_OUTPORT.publishPort();
		batterie_OUTPORT.publishPort();
		panneau_OUTPORT.publishPort();
		action_INPORT.publishPort();
		
		if (AbstractCVM.isDistributed) {
			this.executionLog.setDirectory(System.getProperty("user.dir")) ;
		} else {
			this.executionLog.setDirectory(System.getProperty("user.home")) ;
		}
		
		/** TODO definir pool de thread */
		
		// affichage
		this.tracer.setTitle("Compteur");
		this.tracer.setRelativePosition(1, 3);
	}

	public void ajouterAppareil(String uri) throws Exception {
		this.appareil_consommation.put(uri, 0.0);
	}
	
	public void ajouterUniteProduction(String uri) throws Exception {
		this.unite_production.put(uri, 0.0);
	}

	public double envoyerConsommationGlobale() throws Exception {
		return appareil_consommation.values().stream().mapToDouble(i -> i).sum();
	}

	public double envoyerProductionGlobale() throws Exception {
		return unite_production.values().stream().mapToDouble(i -> i).sum();
	}

	public double getFrigoConsommation() throws Exception {
		return this.frigo_OUTPORT.getFrigoConsommation();
	}

	public double getLaveLingeConsommation() throws Exception {
		return this.lavelinge_OUTPORT.getLaveLingeConsommation();
	}

	public double getOrdinateurConsommation() throws Exception {
		return this.ordinateur_OUTPORT.getOrdinateurConsommation();
	}

	public double getPanneauProduction() throws Exception {
		return this.panneau_OUTPORT.getPanneauProduction();
	}

	public double getBatterieProduction() throws Exception {
		return this.batterie_OUTPORT.getBatterieProduction();
	}
}