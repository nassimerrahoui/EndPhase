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
import fr.sorbonne_u.components.cvm.AbstractCVM;

public class CVM extends AbstractCVM {
	
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
	
	/** URI Port des pour l'interconnexion des JVM **/
	protected static String URI_DATAOUTPORT_FRIGO = "oport1";
	protected static String URI_DATAOUTPORT_ORDINATEUR = "oport2";
	protected static String URI_DATAOUTPORT_CHARGEUR = "oport3";
	protected static String URI_DATAOUTPORT_PANNEAU = "oport4";
	protected static String URI_DATAOUTPORT_BATTERIE = "oport5";
	protected static String URI_DATAOUTPORT_CONTROLEUR = "oport6";
	protected static String URI_DATAOUTPORT_COMPTEUR = "oport7";
	protected static Vector<String> URI_DATAOUTPORTS_COMPTEUR = new Vector<>();
	
	/** Priorite d'allumage par ordre croissant des appareils 
	 ** Contient un tuple <URI de l'appareil, numero du typeappareil>
	 **  **/
	Vector<String[]> priorites = new Vector<>();
	Vector<String[]> uproductions = new Vector<>();

	/**
	 * 
	 * @param nbAppareilAndProduction
	 * @throws Exception
	 */
	public CVM(int nbAppareilAndProduction) throws Exception { 
		super(); 
		
		/** Port Data Sortant pour le compteur **/
		for (int i = 0; i < nbAppareilAndProduction; i++) {
			URI_DATAOUTPORTS_COMPTEUR.add("compteur_oport"+i);
		}
		
	}

	@Override
	public void deploy() throws Exception {

		priorites.add(new String[]{frigoURI,"1"});
		priorites.add(new String[]{chargeurURI,"2"});
		priorites.add(new String[]{ordinateurURI,"3"});
		
		uproductions.add(new String[]{panneauURI,"1"});
		uproductions.add(new String[]{batterieURI,"2"});
		
		controleur = new Controleur(controleurURI, 10, 0, URI_DATAOUTPORT_CONTROLEUR, priorites, uproductions);
		frigo = new Frigo(frigoURI, 1, 0, URI_DATAOUTPORT_FRIGO, TypeAppareil.CONSO_PERMANENTE);
		ordinateur = new Ordinateur(ordinateurURI, 1, 0, URI_DATAOUTPORT_ORDINATEUR, TypeAppareil.CONSO_INCONTROLABLE);
		chargeur = new Chargeur(chargeurURI, 1, 0, URI_DATAOUTPORT_CHARGEUR, TypeAppareil.CONSO_PLANIFIABLE);
		panneau = new PanneauSolaire(panneauURI, 5, 0, URI_DATAOUTPORT_PANNEAU);
		batterie = new Batterie(batterieURI, 5, 0, URI_DATAOUTPORT_BATTERIE);
		compteur = new Compteur(compteurURI, 10, 0, URI_DATAOUTPORTS_COMPTEUR);
		
		this.addDeployedComponent(controleurURI,controleur);
		this.addDeployedComponent(frigoURI,frigo);
		this.addDeployedComponent(ordinateurURI, ordinateur);
		this.addDeployedComponent(chargeurURI, chargeur);
		this.addDeployedComponent(panneauURI, panneau);
		this.addDeployedComponent(batterieURI, batterie);
		this.addDeployedComponent(compteurURI, compteur);
		
		this.toggleTracing(controleurURI);
		this.toggleTracing(frigoURI);
		this.toggleTracing(ordinateurURI);
		this.toggleTracing(chargeurURI);
		this.toggleTracing(panneauURI);
		this.toggleTracing(batterieURI);
		this.toggleTracing(compteurURI);

		
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
		
		super.deploy();
	}
 
	public static void main(String[] args) {
		try {
			CVM cvm = new CVM(5);
			cvm.startStandardLifeCycle(40000L);
			Thread.sleep(5000L);
			System.exit(0);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
