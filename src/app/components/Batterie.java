package app.components;

import java.util.concurrent.TimeUnit;
import app.interfaces.generateur.IEntiteDynamique;
import app.interfaces.production.IAjoutUniteProduction;
import app.interfaces.production.IBatterie;
import app.interfaces.production.IProduction;
import app.ports.batterie.BatterieAssembleurInPort;
import app.ports.batterie.BatterieCompteurOutPort;
import app.ports.batterie.BatterieControleurOutPort;
import app.ports.batterie.BatterieInPort;
import app.util.EtatUniteProduction;
import app.util.URI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.ports.PortI;

@OfferedInterfaces(offered = { IBatterie.class, IEntiteDynamique.class })
@RequiredInterfaces(required = { IAjoutUniteProduction.class, IProduction.class })
public class Batterie extends AbstractComponent {

	/** port sortant permettant a l'unite de s'inscrire sur la liste des unites du controleur */
	protected BatterieControleurOutPort controleur_OUTPORT;
	
	/** port sortant permettant au compteur de recupere la production de l'unite */
	protected BatterieCompteurOutPort production_OUTPORT;
	
	protected EtatUniteProduction etat;
	protected Double production;

	public Batterie(
			String BATTERIE_URI, 
			String BATTERIE_COMPTEUR_OP_URI,
			String BATTERIE_CONTROLEUR_OP_URI,
			int nbThreads, int nbSchedulableThreads) throws Exception {
		super(BATTERIE_URI, nbThreads, nbSchedulableThreads);

		this.controleur_OUTPORT = new BatterieControleurOutPort(BATTERIE_CONTROLEUR_OP_URI,this);
		this.production_OUTPORT = new BatterieCompteurOutPort(BATTERIE_COMPTEUR_OP_URI,this);
		
		// port entrant permettant au controleur d'effectuer des actions sur la batterie
		BatterieInPort action_INPORT = new BatterieInPort(this);
		
		// port entrant permettant a l'assembleur d'effectuer d'integrer l'entite au logement
		BatterieAssembleurInPort launch_INPORT = new BatterieAssembleurInPort(this);
		
		this.addPort(controleur_OUTPORT);
		this.addPort(production_OUTPORT);
		this.addPort(action_INPORT);
		this.addPort(launch_INPORT);
		
		controleur_OUTPORT.publishPort();
		production_OUTPORT.publishPort();
		action_INPORT.publishPort();
		launch_INPORT.publishPort();
		
		if (AbstractCVM.isDistributed) {
			this.executionLog.setDirectory(System.getProperty("user.dir")) ;
		} else {
			this.executionLog.setDirectory(System.getProperty("user.home")) ;
		}
		
		/** TODO definir pool de thread */
		
		// affichage
		this.tracer.setTitle("Batterie");
		this.tracer.setRelativePosition(0, 2);
		this.toggleTracing();
		this.toggleLogging();

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
		this.logMessage("Demarrage de la batterie...");
	}

	@Override
	public void execute() throws Exception {
		super.execute();
		
		this.logMessage("Phase d'execution de la batterie.");
		
		this.logMessage("Execution en cours...");
		
		this.scheduleTaskWithFixedDelay(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try { ((Batterie) this.getTaskOwner()).envoyerProduction(URI.BATTERIE_URI.getURI(), production); } 
				catch (Exception e) { throw new RuntimeException(e); }
			}
		}, 2000, 1000, TimeUnit.MILLISECONDS);
	}
	
	@Override
	public void finalise() throws Exception {
		this.logMessage("Arret du composant batterie...") ;
		super.finalise();
	}
	
	@Override
	public void	shutdown() throws ComponentShutdownException
	{
		try {
			
			PortI[] p1 = this.findPortsFromInterface(IBatterie.class);
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
			
			PortI[] p1 = this.findPortsFromInterface(IBatterie.class);
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
