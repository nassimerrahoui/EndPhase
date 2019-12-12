package app.components;

import app.connectors.AppareilCompteurConnector;
import app.connectors.AppareilControleurConnector;
import app.connectors.AssembleurEntiteConnector;
import app.connectors.ControleurBatterieConnector;
import app.connectors.ControleurCompteurConnector;
import app.connectors.ControleurFrigoConnector;
import app.connectors.ControleurLaveLingeConnector;
import app.connectors.ControleurOrdinateurConnector;
import app.connectors.ControleurPanneauSolaireConnector;
import app.connectors.UniteCompteurConnector;
import app.connectors.UniteControleurConnector;
import app.interfaces.appareil.IFrigo;
import app.interfaces.appareil.ILaveLinge;
import app.interfaces.appareil.IOrdinateur;
import app.interfaces.compteur.ICompteur;
import app.interfaces.compteur.ICompteurControleur;
import app.interfaces.controleur.IControleur;
import app.interfaces.generateur.IAssembleur;
import app.interfaces.production.IBatterie;
import app.interfaces.production.IPanneau;
import app.ports.assembleur.AssembleurOutPort;
import app.util.TypeAppareil;
import app.util.URI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.pre.dcc.connectors.DynamicComponentCreationConnector;
import fr.sorbonne_u.components.pre.dcc.interfaces.DynamicComponentCreationI;
import fr.sorbonne_u.components.pre.dcc.ports.DynamicComponentCreationOutboundPort;
import fr.sorbonne_u.components.reflection.connectors.ReflectionConnector;
import fr.sorbonne_u.components.reflection.interfaces.ReflectionI;
import fr.sorbonne_u.components.reflection.ports.ReflectionOutboundPort;

/**
 * Ce composant permet de creer l'ensembles des autres composants tels que : 
 * les appareils, les unites de production, le controleur et le compteur.
 * Il va egalement les connecter et les lancer.
 */
@RequiredInterfaces(required = { ReflectionI.class, DynamicComponentCreationI.class, IAssembleur.class })

public class Assembleur extends AbstractComponent {

	protected DynamicComponentCreationOutboundPort DynamicOutPort;
	protected AssembleurOutPort AssembleurOutPort;
	
	protected String[] LISTE_REFLECTION_INPORT;
	protected String[] LISTE_JVM_URI;

	protected ReflectionOutboundPort rop;

	public Assembleur(String uri, int nbThreads, int nbScheduleThreads, String[] LISTE_JVM_URI) {
		super(uri, nbThreads, nbScheduleThreads);
		
		this.LISTE_JVM_URI = LISTE_JVM_URI;
		this.LISTE_REFLECTION_INPORT = new String[7];

		try {
			this.AssembleurOutPort = new AssembleurOutPort(URI.DYNAMIC_ASSEMBLEUR_URI.getURI(), this);
			this.AssembleurOutPort.localPublishPort();
		} catch (Exception e) { e.printStackTrace(); }

		this.tracer.setTitle("Assembleur");
		this.tracer.setRelativePosition(0, 3);
	}

	public void dynamicDeploy() throws Exception {
		this.logMessage("Debut du deploiement...");
		int i = 0;

		DynamicOutPort.doConnection(LISTE_JVM_URI[i] + AbstractCVM.DCC_INBOUNDPORT_URI_SUFFIX,
				DynamicComponentCreationConnector.class.getCanonicalName());

		LISTE_REFLECTION_INPORT[0] = DynamicOutPort.createComponent(Controleur.class.getCanonicalName(),
				new Object[] { 
						URI.CONTROLEUR_OP_FRIGO_URI.getURI(),
						URI.CONTROLEUR_OP_LAVELINGE_URI.getURI(),
						URI.CONTROLEUR_OP_ORDINATEUR_URI.getURI(),
						URI.CONTROLEUR_OP_PANNEAUSOLAIRE_URI.getURI(),
						URI.CONTROLEUR_OP_BATTERIE_URI.getURI(),
						URI.CONTROLEUR_OP_COMPTEUR_URI.getURI(),
						Integer.valueOf(5),
						Integer.valueOf(5)});

		i++;
		DynamicOutPort.doDisconnection();
		
		DynamicOutPort.doConnection(LISTE_JVM_URI[i] + AbstractCVM.DCC_INBOUNDPORT_URI_SUFFIX,
				DynamicComponentCreationConnector.class.getCanonicalName());

		LISTE_REFLECTION_INPORT[1] = DynamicOutPort.createComponent(Frigo.class.getCanonicalName(),
				new Object[] { 
						URI.FRIGO_URI.getURI(), 
						URI.FRIGO_COMPTEUR_OP_URI.getURI(),
						URI.FRIGO_CONTROLEUR_OP_URI.getURI(),
						Integer.valueOf(2),
						Integer.valueOf(2),
						TypeAppareil.CONSO_PERMANENTE});

		i++;
		DynamicOutPort.doDisconnection();
		
		DynamicOutPort.doConnection(LISTE_JVM_URI[i] + AbstractCVM.DCC_INBOUNDPORT_URI_SUFFIX,
				DynamicComponentCreationConnector.class.getCanonicalName());

		LISTE_REFLECTION_INPORT[2] = DynamicOutPort.createComponent(LaveLinge.class.getCanonicalName(),
				new Object[] { 
						URI.LAVELIGNE_URI.getURI(), 
						URI.LAVELINGE_COMPTEUR_OP_URI.getURI(),
						URI.LAVELINGE_CONTROLEUR_OP_URI.getURI(),
						Integer.valueOf(2),
						Integer.valueOf(2),
						TypeAppareil.CONSO_PLANIFIABLE});

		i++;
		DynamicOutPort.doDisconnection();
		DynamicOutPort.doConnection(LISTE_JVM_URI[i] + AbstractCVM.DCC_INBOUNDPORT_URI_SUFFIX,
				DynamicComponentCreationConnector.class.getCanonicalName());

		LISTE_REFLECTION_INPORT[3] = DynamicOutPort.createComponent(Ordinateur.class.getCanonicalName(),
				new Object[] { 
						URI.ORDINATEUR_URI.getURI(), 
						URI.ORDINATEUR_COMPTEUR_OP_URI.getURI(),
						URI.ORDINATEUR_CONTROLEUR_OP_URI.getURI(),
						Integer.valueOf(2),
						Integer.valueOf(2),
						TypeAppareil.CONSO_INCONTROLABLE});

		i++;
		DynamicOutPort.doDisconnection();
		DynamicOutPort.doConnection(LISTE_JVM_URI[i] + AbstractCVM.DCC_INBOUNDPORT_URI_SUFFIX,
				DynamicComponentCreationConnector.class.getCanonicalName());

		LISTE_REFLECTION_INPORT[4] = DynamicOutPort.createComponent(PanneauSolaire.class.getCanonicalName(),
				new Object[] { 
						URI.PANNEAUSOLAIRE_URI.getURI(), 
						URI.PANNEAUSOLAIRE_COMPTEUR_OP_URI.getURI(),
						URI.PANNEAUSOLAIRE_CONTROLEUR_OP_URI.getURI(),
						Integer.valueOf(2),
						Integer.valueOf(2)});

		i++;
		DynamicOutPort.doDisconnection();
		DynamicOutPort.doConnection(LISTE_JVM_URI[i] + AbstractCVM.DCC_INBOUNDPORT_URI_SUFFIX,
				DynamicComponentCreationConnector.class.getCanonicalName());

		LISTE_REFLECTION_INPORT[5] = DynamicOutPort.createComponent(Batterie.class.getCanonicalName(),
				new Object[] { 
						URI.BATTERIE_URI.getURI(), 
						URI.BATTERIE_COMPTEUR_OP_URI.getURI(),
						URI.BATTERIE_CONTROLEUR_OP_URI.getURI(),
						Integer.valueOf(2),
						Integer.valueOf(2)});

		i++;
		DynamicOutPort.doDisconnection();
		DynamicOutPort.doConnection(LISTE_JVM_URI[i] + AbstractCVM.DCC_INBOUNDPORT_URI_SUFFIX,
				DynamicComponentCreationConnector.class.getCanonicalName());

		LISTE_REFLECTION_INPORT[6] = DynamicOutPort.createComponent(Compteur.class.getCanonicalName(),
				new Object[] { 
						URI.COMPTEUR_URI.getURI(),
						Integer.valueOf(5),
						Integer.valueOf(5)});

		DynamicOutPort.doDisconnection();

		this.logMessage("Début de l'interconnexion...");
		
		String[] uri;

		this.rop = new ReflectionOutboundPort(this);
		this.rop.localPublishPort();

		rop.doConnection(LISTE_REFLECTION_INPORT[0], ReflectionConnector.class.getCanonicalName());
		
		rop.toggleTracing();
		rop.toggleLogging();

		uri = rop.findInboundPortURIsFromInterface(IFrigo.class);
		rop.doPortConnection(URI.CONTROLEUR_OP_FRIGO_URI.getURI(), uri[0], 
				ControleurFrigoConnector.class.getCanonicalName());

		uri = rop.findInboundPortURIsFromInterface(ILaveLinge.class);
		rop.doPortConnection(URI.CONTROLEUR_OP_LAVELINGE_URI.getURI(), uri[0], 
				ControleurLaveLingeConnector.class.getCanonicalName());

		uri = rop.findInboundPortURIsFromInterface(IOrdinateur.class);
		rop.doPortConnection(URI.CONTROLEUR_OP_ORDINATEUR_URI.getURI(), uri[0], 
				ControleurOrdinateurConnector.class.getCanonicalName());

		uri = rop.findInboundPortURIsFromInterface(IPanneau.class);
		rop.doPortConnection(URI.CONTROLEUR_OP_PANNEAUSOLAIRE_URI.getURI(), uri[0],
				ControleurPanneauSolaireConnector.class.getCanonicalName());

		uri = rop.findInboundPortURIsFromInterface(IBatterie.class);
		rop.doPortConnection(URI.CONTROLEUR_OP_BATTERIE_URI.getURI(), uri[0],
				ControleurBatterieConnector.class.getCanonicalName());

		uri = rop.findInboundPortURIsFromInterface(ICompteurControleur.class);
		rop.doPortConnection(URI.CONTROLEUR_OP_COMPTEUR_URI.getURI(), uri[0],
				ControleurCompteurConnector.class.getCanonicalName());

		this.doPortDisconnection(rop.getPortURI());
		
		// ******************* Entite vers compteur *********************

		rop.doConnection(LISTE_REFLECTION_INPORT[1], ReflectionConnector.class.getCanonicalName());

		rop.toggleTracing();
		rop.toggleLogging();

		uri = rop.findInboundPortURIsFromInterface(ICompteur.class);
		rop.doPortConnection(URI.FRIGO_COMPTEUR_OP_URI.getURI(), uri[0], 
				AppareilCompteurConnector.class.getCanonicalName());

		this.doPortDisconnection(rop.getPortURI());

		rop.doConnection(LISTE_REFLECTION_INPORT[2], ReflectionConnector.class.getCanonicalName());

		rop.toggleTracing();
		rop.toggleLogging();

		uri = rop.findInboundPortURIsFromInterface(ICompteur.class);
		rop.doPortConnection(URI.LAVELINGE_COMPTEUR_OP_URI.getURI(), uri[0], 
				AppareilCompteurConnector.class.getCanonicalName());
		this.doPortDisconnection(rop.getPortURI());

		rop.doConnection(LISTE_REFLECTION_INPORT[3], ReflectionConnector.class.getCanonicalName());

		rop.toggleTracing();
		rop.toggleLogging();

		uri = rop.findInboundPortURIsFromInterface(ICompteur.class);
		rop.doPortConnection(URI.ORDINATEUR_COMPTEUR_OP_URI.getURI(), uri[0], 
				AppareilCompteurConnector.class.getCanonicalName());

		this.doPortDisconnection(rop.getPortURI());

		rop.doConnection(LISTE_REFLECTION_INPORT[4], ReflectionConnector.class.getCanonicalName());

		rop.toggleTracing();
		rop.toggleLogging();

		uri = rop.findInboundPortURIsFromInterface(ICompteur.class);
		rop.doPortConnection(URI.PANNEAUSOLAIRE_COMPTEUR_OP_URI.getURI(), uri[0], 
				UniteCompteurConnector.class.getCanonicalName());

		this.doPortDisconnection(rop.getPortURI());

		rop.doConnection(LISTE_REFLECTION_INPORT[5], ReflectionConnector.class.getCanonicalName());

		rop.toggleTracing();
		rop.toggleLogging();

		uri = rop.findInboundPortURIsFromInterface(ICompteur.class);
		rop.doPortConnection(URI.BATTERIE_COMPTEUR_OP_URI.getURI(), uri[0], 
				UniteCompteurConnector.class.getCanonicalName());

		this.doPortDisconnection(rop.getPortURI());

		// ************************** Entite vers controleur **********************************

		rop.doConnection(LISTE_REFLECTION_INPORT[1], ReflectionConnector.class.getCanonicalName());

		rop.toggleTracing();
		rop.toggleLogging();

		uri = rop.findInboundPortURIsFromInterface(IControleur.class);
		rop.doPortConnection(URI.FRIGO_CONTROLEUR_OP_URI.getURI(), uri[0], 
				AppareilControleurConnector.class.getCanonicalName());

		this.doPortDisconnection(rop.getPortURI());

		rop.doConnection(LISTE_REFLECTION_INPORT[2], ReflectionConnector.class.getCanonicalName());

		rop.toggleTracing();
		rop.toggleLogging();

		uri = rop.findInboundPortURIsFromInterface(IControleur.class);
		rop.doPortConnection(URI.LAVELINGE_CONTROLEUR_OP_URI.getURI(), uri[0], 
				AppareilControleurConnector.class.getCanonicalName());

		this.doPortDisconnection(rop.getPortURI());

		rop.doConnection(LISTE_REFLECTION_INPORT[3], ReflectionConnector.class.getCanonicalName());

		rop.toggleTracing();
		rop.toggleLogging();

		uri = rop.findInboundPortURIsFromInterface(IControleur.class);
		rop.doPortConnection(URI.ORDINATEUR_CONTROLEUR_OP_URI.getURI(), uri[0], 
				AppareilControleurConnector.class.getCanonicalName());

		this.doPortDisconnection(rop.getPortURI());

		rop.doConnection(LISTE_REFLECTION_INPORT[4], ReflectionConnector.class.getCanonicalName());

		rop.toggleTracing();
		rop.toggleLogging();

		uri = rop.findInboundPortURIsFromInterface(IControleur.class);
		rop.doPortConnection(URI.PANNEAUSOLAIRE_CONTROLEUR_OP_URI.getURI(), uri[0], 
				UniteControleurConnector.class.getCanonicalName());

		this.doPortDisconnection(rop.getPortURI());

		rop.doConnection(LISTE_REFLECTION_INPORT[5], ReflectionConnector.class.getCanonicalName());

		rop.toggleTracing();
		rop.toggleLogging();

		uri = rop.findInboundPortURIsFromInterface(IControleur.class);
		rop.doPortConnection(URI.BATTERIE_CONTROLEUR_OP_URI.getURI(), uri[0], 
				UniteControleurConnector.class.getCanonicalName());

		this.doPortDisconnection(rop.getPortURI());

		// *************** Lancement des actions depuis l'assembleur ****************

		this.logMessage("Ajout des appareils et des unites de production au systeme...");
		
		this.doPortConnection(this.AssembleurOutPort.getPortURI(), URI.FRIGO_URI.getURI(),
				AssembleurEntiteConnector.class.getCanonicalName());
		this.AssembleurOutPort.ajoutLogement(URI.FRIGO_URI.getURI());
		this.doPortDisconnection(this.AssembleurOutPort.getPortURI());

		this.doPortConnection(this.AssembleurOutPort.getPortURI(), URI.LAVELIGNE_URI.getURI(),
				AssembleurEntiteConnector.class.getCanonicalName());
		this.AssembleurOutPort.ajoutLogement(URI.LAVELIGNE_URI.getURI());
		this.doPortDisconnection(this.AssembleurOutPort.getPortURI());

		this.doPortConnection(this.AssembleurOutPort.getPortURI(), URI.ORDINATEUR_URI.getURI(),
				AssembleurEntiteConnector.class.getCanonicalName());
		this.AssembleurOutPort.ajoutLogement(URI.ORDINATEUR_URI.getURI());
		this.doPortDisconnection(this.AssembleurOutPort.getPortURI());

		this.doPortConnection(this.AssembleurOutPort.getPortURI(), URI.PANNEAUSOLAIRE_URI.getURI(),
				AssembleurEntiteConnector.class.getCanonicalName());
		this.AssembleurOutPort.ajoutLogement(URI.PANNEAUSOLAIRE_URI.getURI());
		this.doPortDisconnection(this.AssembleurOutPort.getPortURI());

		this.doPortConnection(this.AssembleurOutPort.getPortURI(), URI.BATTERIE_URI.getURI(),
				AssembleurEntiteConnector.class.getCanonicalName());
		this.AssembleurOutPort.ajoutLogement(URI.BATTERIE_URI.getURI());
		this.doPortDisconnection(this.AssembleurOutPort.getPortURI());
	}

	@Override
	public void start() throws ComponentStartException {
		this.logMessage("Activation de l'assembleur...");
		try {
			DynamicOutPort = new DynamicComponentCreationOutboundPort(this);
			DynamicOutPort.localPublishPort();

		} catch (Exception e) {
			throw new ComponentStartException(e);
		}

		super.start();
	}
}