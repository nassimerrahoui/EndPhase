package app.components;

import java.util.Vector;
import app.interfaces.controleur.IControleCompteur;
import app.interfaces.controleur.IControleFrigo;
import app.interfaces.controleur.IControleLaveLinge;
import app.interfaces.controleur.IControleOrdinateur;
import app.interfaces.controleur.IControleur;
import app.ports.controleur.ControleurFrigoOutPort;
import app.ports.controleur.ControleurLaveLingeOutPort;
import app.ports.controleur.ControleurOrdiOutPort;
import app.util.EtatAppareil;
import app.util.EtatUniteProduction;
import app.util.ModeFrigo;
import app.util.ModeLaveLinge;
import app.util.ModeOrdinateur;
import app.util.TemperatureLaveLinge;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cvm.AbstractCVM;

@OfferedInterfaces(offered = { IControleur.class })
@RequiredInterfaces(required = { 
		IControleCompteur.class, 
		IControleFrigo.class, 
		IControleLaveLinge.class, 
		IControleOrdinateur.class })

public class Controleur extends AbstractComponent {
	
	protected ControleurFrigoOutPort frigo_OUTPORT;
	protected ControleurLaveLingeOutPort lavelinge_OUTPORT;
	protected ControleurOrdiOutPort ordinateur_OUTPORT;
	protected ControleurPanneauoOutPort panneausolaire_OUTPORT;
	protected ControleurBatterieOutPort batterie_OUTPORT;

	protected Vector<String> unitesProduction = new Vector<>();
	protected Vector<String> appareils = new Vector<>();

	public Controleur(String controleurURI, int nbThreads, int nbSchedulableThreads) throws Exception {
		super(controleurURI, nbThreads, nbSchedulableThreads);

		if (AbstractCVM.isDistributed) {
			this.executionLog.setDirectory(System.getProperty("user.dir")) ;
		} else {
			this.executionLog.setDirectory(System.getProperty("user.home")) ;
		}
		
		/** TODO definir pool de thread */
		
		// affichage
		this.tracer.setTitle("Controleur");
		this.tracer.setRelativePosition(1, 0);
	}
	
	// ******* Services requis pour allumer ou eteindre des appareils ou unites de production *********

	public void envoyerEtatAppareil(EtatAppareil etat) throws Exception {
		// TODO Auto-generated method stub
	}

	public void envoyerEtatUniteProduction(EtatUniteProduction etat) throws Exception {
		// TODO Auto-generated method stub		
	}

	// ******* Services requis pour effectuer des actions sur lave-linge *********
	
	public void envoyerPlanificationCycle(double heure) throws Exception {
		// TODO Auto-generated method stub		
	}

	public void envoyerPlanificationMode(ModeLaveLinge ml, double heure) throws Exception {
		// TODO Auto-generated method stub		
	}

	public void envoyerTemperature(TemperatureLaveLinge tl) throws Exception {
		// TODO Auto-generated method stub		
	}
	
	// ******* Services requis pour effectuer des actions sur frigo *********

	public void envoyerTemperature_Refrigerateur(double temperature) throws Exception {
		this.frigo_OUTPORT.envoyerTemperature_Refrigerateur(temperature);
	}

	public void envoyerTemperature_Congelateur(double temperature) throws Exception {
		this.frigo_OUTPORT.envoyerTemperature_Congelateur(temperature);
	}

	public void envoyerLumiere_Refrigerateur(ModeFrigo mf) throws Exception {
		this.frigo_OUTPORT.envoyerLumiere_Refrigerateur(mf);
	}

	public void envoyerLumiere_Congelateur(ModeFrigo mf) throws Exception {
		this.frigo_OUTPORT.envoyerLumiere_Congelateur(mf);		
	}
	
	// ******* Services requis pour effectuer des actions sur ordinateur *********

	public void envoyerMode(ModeOrdinateur mo) throws Exception {
		this.ordinateur_OUTPORT.envoyerMode(mo);	
	}
	
	// ******* Services requis pour recuperer les informations du compteur *********
	
	public void getAllConsommations() throws Exception {
		// TODO Auto-generated method stub
	}

	public void getAllProductions() throws Exception {
		// TODO Auto-generated method stub
	}
	
	// ******* Service offert pour les appareils *********

	public void ajouterAppareil(String uri) throws Exception {
		this.appareils.add(uri);
	}
	
	// ******* Service offert pour les unites de production  *********

	public void ajouterUniteProduction(String uri) throws Exception {
		this.unitesProduction.add(uri);
	}
}
