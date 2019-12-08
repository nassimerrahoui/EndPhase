package app.components;

import java.util.concurrent.TimeUnit;
import app.interfaces.production.IAjoutUniteProduction;
import app.interfaces.production.IPanneau;
import app.interfaces.production.IProduction;
import app.ports.panneausolaire.PanneauCompteurOutPort;
import app.ports.panneausolaire.PanneauControleurOutPort;
import app.ports.panneausolaire.PanneauInPort;
import app.util.EtatUniteProduction;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.ports.PortI;

@OfferedInterfaces(offered = { IPanneau.class })
@RequiredInterfaces(required = { IAjoutUniteProduction.class, IProduction.class })
public class PanneauSolaire extends AbstractComponent {

	/** port sortant permettant a l'unite de s'inscrire sur la liste des unites du controleur */
	protected PanneauControleurOutPort controleur_OUTPORT;
	
	/** port sortant permettant au compteur de recupere la production de l'unite */
	protected PanneauCompteurOutPort compteur_OUTPORT;
	
	protected EtatUniteProduction etat;
	protected Double production;

	public PanneauSolaire(String panneauURI, 
			int nbThreads, int nbSchedulableThreads) throws Exception {
		super(panneauURI, nbThreads, nbSchedulableThreads);

		this.controleur_OUTPORT = new PanneauControleurOutPort(this);
		this.compteur_OUTPORT = new PanneauCompteurOutPort(this);
		
		// port entrant permettant au controleur d'effectuer des actions sur le panneau solaire
		PanneauInPort action_INPORT = new PanneauInPort(this);
		
		this.addPort(controleur_OUTPORT);
		this.addPort(compteur_OUTPORT);
		this.addPort(action_INPORT);
		
		controleur_OUTPORT.publishPort();
		compteur_OUTPORT.publishPort();
		action_INPORT.publishPort();
		
		if (AbstractCVM.isDistributed) {
			this.executionLog.setDirectory(System.getProperty("user.dir")) ;
		} else {
			this.executionLog.setDirectory(System.getProperty("user.home")) ;
		}
		
		/** TODO definir pool de thread */
		
		// affichage
		this.tracer.setTitle("Panneau Solaire");
		this.tracer.setRelativePosition(1, 2);

		// attributs
		etat = EtatUniteProduction.OFF;
		production = 0.0;
	}
	

	public void demandeAjoutControleur(String uri) throws Exception {
		this.controleur_OUTPORT.demandeAjoutControleur(uri);
	}

	public void envoyerProduction(String uri, double production) throws Exception {
		this.compteur_OUTPORT.envoyerProduction(uri, production);
	}
	
	public void setEtatUniteProduction(EtatUniteProduction etat) throws Exception {
		this.etat = etat;
	}
	
	// ************* Cycle de vie du composant ************* 

	@Override
	public void start() throws ComponentStartException {
		super.start();
		
		this.logMessage("Demarrage du panneau solaire...");

		this.scheduleTask(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try { ((PanneauSolaire) this.getTaskOwner()).demandeAjoutControleur("CONSTANTE A METTRE ICI"); }
				catch (Exception e) { throw new RuntimeException(e); }
			}
		}, 1000, TimeUnit.MILLISECONDS);
	}

	@Override
	public void execute() throws Exception {
		super.execute();
		
		this.logMessage("Phase d'execution du panneau solaire.");
		
		this.logMessage("Execution en cours...");
		
		this.scheduleTaskWithFixedDelay(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try { ((PanneauSolaire) this.getTaskOwner()).envoyerProduction("CONSTANTE A METTRE ICI", production); } 
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
			
			PortI[] p1 = this.findPortsFromInterface(IPanneau.class);
			PortI[] p2 = this.findPortsFromInterface(IAjoutUniteProduction.class);
			PortI[] p3 = this.findPortsFromInterface(IProduction.class);
			
			p1[0].unpublishPort();
			p2[0].unpublishPort();
			p3[0].unpublishPort();
			
		} catch (Exception e) { throw new ComponentShutdownException(e); }
		super.shutdownNow();
	}
}
