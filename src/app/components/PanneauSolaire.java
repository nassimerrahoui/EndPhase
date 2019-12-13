package app.components;

import java.util.concurrent.TimeUnit;
import app.interfaces.generateur.IEntiteDynamique;
import app.interfaces.production.IAjoutUniteProduction;
import app.interfaces.production.IPanneau;
import app.interfaces.production.IProduction;
import app.ports.panneausolaire.PanneauAssembleurInPort;
import app.ports.panneausolaire.PanneauCompteurOutPort;
import app.ports.panneausolaire.PanneauControleurOutPort;
import app.ports.panneausolaire.PanneauInPort;
import app.util.EtatUniteProduction;
import app.util.URI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.ports.PortI;

@OfferedInterfaces(offered = { IPanneau.class, IEntiteDynamique.class })
@RequiredInterfaces(required = { IAjoutUniteProduction.class, IProduction.class })
public class PanneauSolaire extends AbstractComponent {

	/** port sortant permettant a l'unite de s'inscrire sur la liste des unites du controleur */
	protected PanneauControleurOutPort controleur_OUTPORT;
	
	/** port sortant permettant au compteur de recupere la production de l'unite */
	protected PanneauCompteurOutPort production_OUTPORT;
	
	protected EtatUniteProduction etat;
	protected Double production;

	protected PanneauSolaire(
			String PANNEAUSOLAIRE_URI, 
			String PANNEAUSOLAIRE_COMPTEUR_OP_URI,
			String PANNEAUSOLAIRE_CONTROLEUR_OP_URI,
			int nbThreads, int nbSchedulableThreads) throws Exception {
		super(PANNEAUSOLAIRE_URI, nbThreads, nbSchedulableThreads);

		this.controleur_OUTPORT = new PanneauControleurOutPort(PANNEAUSOLAIRE_CONTROLEUR_OP_URI,this);
		this.production_OUTPORT = new PanneauCompteurOutPort(PANNEAUSOLAIRE_COMPTEUR_OP_URI,this);
		
		// port entrant permettant au controleur d'effectuer des actions sur le panneau solaire
		PanneauInPort action_INPORT = new PanneauInPort(this);
		
		// port entrant permettant a l'assembleur d'effectuer d'integrer l'entite au logement
		PanneauAssembleurInPort launch_INPORT = new PanneauAssembleurInPort(this);
		
		controleur_OUTPORT.publishPort();
		production_OUTPORT.publishPort();
		action_INPORT.publishPort();
		launch_INPORT.publishPort();
		
		if (AbstractCVM.isDistributed) {
			this.executionLog.setDirectory(System.getProperty("user.dir")) ;
		} else {
			this.executionLog.setDirectory(System.getProperty("user.home")) ;
		}
		
		this.createNewExecutorService(URI.POOL_ACTION_PANNEAUSOLAIRE_URI.getURI(), 5, false) ;
		
		// affichage
		this.tracer.setTitle("Panneau Solaire");
		this.tracer.setRelativePosition(1, 2);
		this.toggleTracing();

		// attributs
		etat = EtatUniteProduction.OFF;
		production = 0.0;
	}
	

	public void demandeAjoutControleur(String uri) throws Exception {
		this.controleur_OUTPORT.demandeAjoutControleur(uri);
	}

	public void envoyerProduction(String uri, double production) throws Exception {
		this.production_OUTPORT.envoyerProduction(uri, production);
	}
	
	public void setEtatUniteProduction(EtatUniteProduction etat) throws Exception {
		this.etat = etat;
	}
	
	// ************* Cycle de vie du composant ************* 

	@Override
	public void start() throws ComponentStartException {
		super.start();
		this.logMessage("Demarrage du panneau solaire...");
		
		this.logMessage("Phase d'execution du panneau solaire.");
		
		this.logMessage("Execution en cours...");
		
		this.scheduleTaskWithFixedDelay(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try { ((PanneauSolaire) this.getTaskOwner()).envoyerProduction(URI.PANNEAUSOLAIRE_URI.getURI(), production); } 
				catch (Exception e) { throw new RuntimeException(e); }
			}
		}, 2000, 1000, TimeUnit.MILLISECONDS);
	}

	@Override
	public void finalise() throws Exception {
		this.logMessage("Arret du composant panneau solaire...") ;
		super.finalise();
	}
	
	@Override
	public void	shutdown() throws ComponentShutdownException
	{
		try {
			
			PortI[] p1 = this.findPortsFromInterface(IPanneau.class);
			PortI[] p2 = this.findPortsFromInterface(IAjoutUniteProduction.class);
			PortI[] p3 = this.findPortsFromInterface(IProduction.class);
			PortI[] port_assembleur = this.findPortsFromInterface(IEntiteDynamique.class);
			
			p1[0].unpublishPort();
			p2[0].unpublishPort();
			p3[0].unpublishPort();
			port_assembleur[0].unpublishPort();
			
		} catch (Exception e) { throw new ComponentShutdownException(e); }
		super.shutdown();
	}

	@Override
	public void shutdownNow() throws ComponentShutdownException
	{
		try {
			
			PortI[] p1 = this.findPortsFromInterface(IPanneau.class);
			PortI[] p2 = this.findPortsFromInterface(IAjoutUniteProduction.class);
			PortI[] p3 = this.findPortsFromInterface(IProduction.class);
			PortI[] port_assembleur = this.findPortsFromInterface(IEntiteDynamique.class);
			
			p1[0].unpublishPort();
			p2[0].unpublishPort();
			p3[0].unpublishPort();
			port_assembleur[0].unpublishPort();
			
		} catch (Exception e) { throw new ComponentShutdownException(e); }
		super.shutdownNow();
	}
}
