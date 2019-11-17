package app;

import java.util.Vector;
import app.components.Batterie;
import app.components.Chargeur;
import app.components.Compteur;
import app.components.Controleur;
import app.components.Frigo;
import app.components.Ordinateur;
import app.components.PanneauSolaire;
import app.connectors.DataServiceConnector;
import app.util.TypeAppareil;
import fr.sorbonne_u.components.cvm.AbstractDistributedCVM;

public class DistributedCVM extends AbstractDistributedCVM {
	
	// Composants
	protected static Controleur controleur;
	protected static Frigo frigo;
	protected static Ordinateur ordinateur;
	protected static Chargeur chargeur;
	protected static PanneauSolaire panneau;
	protected static Batterie batterie;
	protected static Compteur compteur;
	
	// URI des composants -> TODO a generer dans le constructeur des composants
	String controleurURI = "controleurURI";
	String frigoURI = "frigoURI";
	String ordinateurURI = "ordinateurURI";
	String chargeurURI = "chargeurURI";
	String compteurURI = "compteurURI";
	String panneauURI = "panneauURI";
	String batterieURI = "batterieURI";

	// URI des JVM defini dans le fichier config.xml
	protected static String JVM_1 = "controleur";
	protected static String JVM_2 = "frigo";
	protected static String JVM_3 = "ordinateur";
	protected static String JVM_4 = "chargeur";
	protected static String JVM_5 = "compteur";
	protected static String JVM_6 = "panneau";
	protected static String JVM_7 = "batterie";
	
	// URI Port des pour l'interconnexion des JVM
	protected static String URI_DATAOUTPORT_FRIGO = "oport6";
	protected static String URI_DATAOUTPORT_ORDINATEUR = "oport5";
	protected static String URI_DATAOUTPORT_CHARGEUR = "oport4";
	protected static String URI_DATAOUTPORT_PANNEAU = "oport3";
	protected static String URI_DATAOUTPORT_BATTERIE = "oport2";
	protected static String URI_DATAOUTPORT_CONTROLEUR = "oport1";
	protected static Vector<String> URI_DATAOUTPORTS_COMPTEUR;
	
	/** Priorite d'allumage par ordre croissant des appareils 
	 ** Contient un tuple <URI de l'appareil, numero du typeappareil>
	 **  **/
	Vector<String[]> priorites = new Vector<>();
	Vector<String[]> uproductions = new Vector<>();

	public DistributedCVM(String[] args, int xLayout, int yLayout, int nbAppareilAndProduction) throws Exception {
		super(args, xLayout, yLayout);
		
		/** Port Data Sortant pour le compteur **/
		for (int i = 0; i < nbAppareilAndProduction; i++) {
			URI_DATAOUTPORTS_COMPTEUR.add("compteur_oport"+i);
		}
	}

	@Override
	public void initialise() throws Exception {
		// debugging mode configuration
		// AbstractCVM.DEBUG_MODE.add(CVMDebugModes.PUBLIHSING);
		// AbstractCVM.DEBUG_MODE.add(CVMDebugModes.CONNECTING);
		// AbstractCVM.DEBUG_MODE.add(CVMDebugModes.COMPONENT_DEPLOYMENT);
		super.initialise();
		priorites.add(new String[]{frigoURI,"1"});
		priorites.add(new String[]{chargeurURI,"2"});
		priorites.add(new String[]{ordinateurURI,"3"});
		
		uproductions.add(new String[]{panneauURI,"1"});
		uproductions.add(new String[]{batterieURI,"2"});
	}

	@Override
	public void instantiateAndPublish() throws Exception {
		if (thisJVMURI.equals(JVM_1)) {
			controleur = new Controleur(controleurURI, 10, 0, URI_DATAOUTPORT_CONTROLEUR, priorites, uproductions);
			this.addDeployedComponent(controleurURI,controleur);
			this.toggleTracing(controleurURI);

		} else if (thisJVMURI.equals(JVM_2)) {
			frigo = new Frigo(frigoURI, 1, 0, URI_DATAOUTPORT_FRIGO, TypeAppareil.CONSO_PERMANENTE);
			this.addDeployedComponent(frigoURI,frigo);
			this.toggleTracing(frigoURI);

		} else if (thisJVMURI.equals(JVM_3)) {
			ordinateur = new Ordinateur(ordinateurURI, 1, 0, URI_DATAOUTPORT_ORDINATEUR, TypeAppareil.CONSO_INCONTROLABLE);
			this.addDeployedComponent(ordinateurURI,ordinateur);
			this.toggleTracing(ordinateurURI);

		} else if (thisJVMURI.equals(JVM_4)) {
			chargeur = new Chargeur(chargeurURI, 1, 0, URI_DATAOUTPORT_CHARGEUR, TypeAppareil.CONSO_PLANIFIABLE);
			this.addDeployedComponent(chargeurURI,chargeur);
			this.toggleTracing(chargeurURI);
		
		} else if (thisJVMURI.equals(JVM_5)) {
			panneau = new PanneauSolaire(panneauURI, 5, 0, URI_DATAOUTPORT_PANNEAU);
			this.addDeployedComponent(panneauURI,panneau);
			this.toggleTracing(chargeurURI);

		} else if (thisJVMURI.equals(JVM_6)) {
			batterie = new Batterie(batterieURI, 5, 0, URI_DATAOUTPORT_BATTERIE);
			this.addDeployedComponent(batterieURI,batterie);
			this.toggleTracing(batterieURI);
	
		} else if (thisJVMURI.equals(JVM_7)) {
			compteur = new Compteur(compteurURI, 10, 0, URI_DATAOUTPORTS_COMPTEUR);
			this.addDeployedComponent(compteurURI,compteur);
			this.toggleTracing(compteurURI);
	
		} else { System.out.println("JVM URI inconnu... " + thisJVMURI); }
		
		super.instantiateAndPublish();
	}

	@Override
	public void interconnect() throws Exception {
		if (thisJVMURI.equals(JVM_1)) {
			this.doPortConnection(
					controleurURI,
					controleur.dataInPorts.get(frigoURI).getPortURI(),
					URI_DATAOUTPORT_FRIGO,
					DataServiceConnector.class.getCanonicalName()) ;
			
			this.doPortConnection(
					controleurURI,
					controleur.dataInPorts.get(ordinateurURI).getPortURI(),
					URI_DATAOUTPORT_ORDINATEUR,
					DataServiceConnector.class.getCanonicalName()) ;
			
			this.doPortConnection(
					controleurURI,
					controleur.dataInPorts.get(chargeurURI).getPortURI(),
					URI_DATAOUTPORT_CHARGEUR,
					DataServiceConnector.class.getCanonicalName()) ;
			
			this.doPortConnection(
					controleurURI,
					controleur.dataInPorts.get(batterieURI).getPortURI(),
					URI_DATAOUTPORT_BATTERIE,
					DataServiceConnector.class.getCanonicalName()) ;
			
			this.doPortConnection(
					controleurURI,
					controleur.dataInPorts.get(panneauURI).getPortURI(),
					URI_DATAOUTPORT_PANNEAU,
					DataServiceConnector.class.getCanonicalName()) ;
			
			this.doPortConnection(
					batterieURI,
					batterie.dataInPort.getPortURI(),
					URI_DATAOUTPORTS_COMPTEUR.get(0),
					DataServiceConnector.class.getCanonicalName()) ;
			
			this.doPortConnection(
					panneauURI,
					panneau.dataInPort.getPortURI(),
					URI_DATAOUTPORTS_COMPTEUR.get(1),
					DataServiceConnector.class.getCanonicalName()) ;
			
			this.doPortConnection(
					frigoURI,
					frigo.dataInPort.getPortURI(),
					URI_DATAOUTPORTS_COMPTEUR.get(2),
					DataServiceConnector.class.getCanonicalName()) ;
			
			this.doPortConnection(
					ordinateurURI,
					ordinateur.dataInPort.getPortURI(),
					URI_DATAOUTPORTS_COMPTEUR.get(3),
					DataServiceConnector.class.getCanonicalName()) ;
			
			this.doPortConnection(
					chargeurURI,
					chargeur.dataInPort.getPortURI(),
					URI_DATAOUTPORTS_COMPTEUR.get(4),
					DataServiceConnector.class.getCanonicalName()) ;
			
			this.doPortConnection(
					compteurURI,
					compteur.dataInPort.getPortURI(),
					URI_DATAOUTPORT_CONTROLEUR,
					DataServiceConnector.class.getCanonicalName()) ;

		}

		super.interconnect();
	}

	public static void main(String[] args) {
		try {
			DistributedCVM da = new DistributedCVM(args, 2, 5, 5);
			da.startStandardLifeCycle(15000L);
			Thread.sleep(10000L);
			System.exit(0);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
