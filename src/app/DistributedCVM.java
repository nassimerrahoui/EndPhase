package app;

import app.components.Batterie;
import app.components.Chargeur;
import app.components.Compteur;
import app.components.Controleur;
import app.components.Frigo;
import app.components.Ordinateur;
import app.components.PanneauSolaire;
import app.connectors.DataServiceConnector;
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
	protected static String URI_DATAOUTPORT_FRIGO = "oport1";
	protected static String URI_DATAOUTPORT_ORDINATEUR = "oport2";
	protected static String URI_DATAOUTPORT_CHARGEUR = "oport3";
	protected static String URI_DATAOUTPORT_PANNEAU = "oport4";
	protected static String URI_DATAOUTPORT_BATTERIE = "oport5";
	protected static String URI_DATAOUTPORT_CONTROLEUR = "oport6";
	protected static String URI_DATAOUTPORT_COMPTEUR = "oport7";

	public DistributedCVM(String[] args, int xLayout, int yLayout) throws Exception {
		super(args, xLayout, yLayout);
	}

	@Override
	public void initialise() throws Exception {
		// debugging mode configuration
		// AbstractCVM.DEBUG_MODE.add(CVMDebugModes.PUBLIHSING);
		// AbstractCVM.DEBUG_MODE.add(CVMDebugModes.CONNECTING);
		// AbstractCVM.DEBUG_MODE.add(CVMDebugModes.COMPONENT_DEPLOYMENT);
		super.initialise();
	}

	@Override
	public void instantiateAndPublish() throws Exception {
		if (thisJVMURI.equals(JVM_1)) {
			controleur = new Controleur(controleurURI, 1, 0, 3);
			this.addDeployedComponent(controleurURI,controleur);
			this.toggleTracing(controleurURI);

		} else if (thisJVMURI.equals(JVM_2)) {
			frigo = new Frigo(frigoURI, 1, 0, URI_DATAOUTPORT_FRIGO);
			this.addDeployedComponent(frigoURI,frigo);
			this.toggleTracing(frigoURI);

		} else if (thisJVMURI.equals(JVM_3)) {
			ordinateur = new Ordinateur(ordinateurURI, 1, 0, URI_DATAOUTPORT_ORDINATEUR);
			this.addDeployedComponent(ordinateurURI,ordinateur);
			this.toggleTracing(ordinateurURI);

		} else if (thisJVMURI.equals(JVM_4)) {
			chargeur = new Chargeur(chargeurURI, 1, 0, URI_DATAOUTPORT_CHARGEUR);
			this.addDeployedComponent(chargeurURI,chargeur);
			this.toggleTracing(chargeurURI);
		
		} else if (thisJVMURI.equals(JVM_5)) {
			panneau = new PanneauSolaire(panneauURI, 1, 0/*, URI_DATAOUTPORT_PANNEAU*/);
			this.addDeployedComponent(panneauURI,panneau);
			this.toggleTracing(chargeurURI);

		} else if (thisJVMURI.equals(JVM_6)) {
			batterie = new Batterie(batterieURI, 1, 0/*, URI_DATAOUTPORT_CHARGEUR*/);
			this.addDeployedComponent(batterieURI,batterie);
			this.toggleTracing(batterieURI);
	
		} else if (thisJVMURI.equals(JVM_7)) {
			compteur = new Compteur(compteurURI, 1, 0, null/*, URI_DATAOUTPORT_CHARGEUR*/);
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
					controleur.dataInPorts.get(0).getPortURI(),
					URI_DATAOUTPORT_FRIGO,
					DataServiceConnector.class.getCanonicalName()) ;
			
			this.doPortConnection(
					controleurURI,
					controleur.dataInPorts.get(1).getPortURI(),
					URI_DATAOUTPORT_ORDINATEUR,
					DataServiceConnector.class.getCanonicalName()) ;
			
			this.doPortConnection(
					controleurURI,
					controleur.dataInPorts.get(2).getPortURI(),
					URI_DATAOUTPORT_CHARGEUR,
					DataServiceConnector.class.getCanonicalName()) ;


		}

		super.interconnect();
	}

	public static void main(String[] args) {
		try {
			DistributedCVM da = new DistributedCVM(args, 2, 5);
			da.startStandardLifeCycle(15000L);
			Thread.sleep(10000L);
			System.exit(0);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
