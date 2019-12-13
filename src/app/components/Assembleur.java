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
import app.interfaces.generateur.IEntiteDynamique;
import app.interfaces.production.IBatterie;
import app.interfaces.production.IPanneau;
import app.ports.assembleur.AssembleurOutPort;
import app.util.TypeAppareil;
import app.util.URI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
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
@RequiredInterfaces(required = { DynamicComponentCreationI.class, 
								IAssembleur.class,
								ReflectionI.class})

public class Assembleur extends AbstractComponent {

	protected DynamicComponentCreationOutboundPort DynamicOutPort;
	
	protected String[] LISTE_REFLECTION_INPORT;
	protected String[] LISTE_JVM_URI;
	protected String[] launch_uri_inport;

	protected Assembleur(String uri, String[] LISTE_JVM_URI) {
		super(uri, 10, 10);
		
		this.LISTE_JVM_URI = LISTE_JVM_URI;
		this.LISTE_REFLECTION_INPORT = new String[7];

		this.tracer.setTitle("Assembleur");
		this.tracer.setRelativePosition(0, 3);
		this.toggleTracing();
	}
	
	@Override
	public void start() throws ComponentStartException {
		super.start();

		this.logMessage("Activation de l'assembleur...");
		
		try {
			
			this.DynamicOutPort = new DynamicComponentCreationOutboundPort(this);
			this.DynamicOutPort.localPublishPort();
			
			this.runTask(new AbstractComponent.AbstractTask() {
				@Override
				public void run() {
					try {
						((Assembleur) this.getTaskOwner()).dynamicDeploy();
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			});

		} catch (Exception e) {
			throw new ComponentStartException(e);
		}
	}

	public void dynamicDeploy() throws Exception {
		
		int i = 0;
		
		DynamicOutPort.doConnection( 
		LISTE_JVM_URI[i] + AbstractCVM.DCC_INBOUNDPORT_URI_SUFFIX,
		DynamicComponentCreationConnector.class.getCanonicalName());
		
		LISTE_REFLECTION_INPORT[0] = DynamicOutPort.createComponent(Controleur.class.getCanonicalName(),
				new Object[] { 
						URI.CONTROLEUR_URI.getURI(),
						URI.CONTROLEUR_OP_FRIGO_URI.getURI(),
						URI.CONTROLEUR_OP_LAVELINGE_URI.getURI(),
						URI.CONTROLEUR_OP_ORDINATEUR_URI.getURI(),
						URI.CONTROLEUR_OP_PANNEAUSOLAIRE_URI.getURI(),
						URI.CONTROLEUR_OP_BATTERIE_URI.getURI(),
						URI.CONTROLEUR_OP_COMPTEUR_URI.getURI(),
						Integer.valueOf(10),
						Integer.valueOf(10)});
		
		i++;
		DynamicOutPort.doDisconnection();

		DynamicOutPort.doConnection( 
				LISTE_JVM_URI[i] + AbstractCVM.DCC_INBOUNDPORT_URI_SUFFIX,
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
		
		DynamicOutPort.doConnection( 
				LISTE_JVM_URI[i] + AbstractCVM.DCC_INBOUNDPORT_URI_SUFFIX,
				DynamicComponentCreationConnector.class.getCanonicalName());
		
		LISTE_REFLECTION_INPORT[2] = DynamicOutPort.createComponent(LaveLinge.class.getCanonicalName(),
				new Object[] { 
						URI.LAVELINGE_URI.getURI(), 
						URI.LAVELINGE_COMPTEUR_OP_URI.getURI(),
						URI.LAVELINGE_CONTROLEUR_OP_URI.getURI(),
						Integer.valueOf(2),
						Integer.valueOf(2),
						TypeAppareil.CONSO_PLANIFIABLE});

		i++;
		DynamicOutPort.doDisconnection();
		
		DynamicOutPort.doConnection( 
				LISTE_JVM_URI[i] + AbstractCVM.DCC_INBOUNDPORT_URI_SUFFIX,
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
		
		DynamicOutPort.doConnection( 
				LISTE_JVM_URI[i] + AbstractCVM.DCC_INBOUNDPORT_URI_SUFFIX,
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
		
		DynamicOutPort.doConnection( 
				LISTE_JVM_URI[i] + AbstractCVM.DCC_INBOUNDPORT_URI_SUFFIX,
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
		
		DynamicOutPort.doConnection( 
				LISTE_JVM_URI[i] + AbstractCVM.DCC_INBOUNDPORT_URI_SUFFIX,
				DynamicComponentCreationConnector.class.getCanonicalName());
		
		LISTE_REFLECTION_INPORT[6] = DynamicOutPort.createComponent(Compteur.class.getCanonicalName(),
				new Object[] { 
						URI.COMPTEUR_URI.getURI(),
						Integer.valueOf(5),
						Integer.valueOf(5)});

		DynamicOutPort.doDisconnection();
		
		this.logMessage("Debut du deploiement...");
		
		// Recuperation des ports entrants des entites pour le controleur et pour l'assembleur
		
		String[] entite_uri_inport = new String[6];
		this.launch_uri_inport = new String[5];
		

		ReflectionOutboundPort rop = new ReflectionOutboundPort(this);
		rop.localPublishPort();
		
		rop.doConnection(LISTE_REFLECTION_INPORT[1], ReflectionConnector.class.getCanonicalName());
		entite_uri_inport[0] = rop.findInboundPortURIsFromInterface(IFrigo.class)[0];
		launch_uri_inport[0] = rop.findInboundPortURIsFromInterface(IEntiteDynamique.class)[0];
		rop.doDisconnection();
		
		rop.doConnection(LISTE_REFLECTION_INPORT[2], ReflectionConnector.class.getCanonicalName());
		entite_uri_inport[1] = rop.findInboundPortURIsFromInterface(ILaveLinge.class)[0];
		launch_uri_inport[1] = rop.findInboundPortURIsFromInterface(IEntiteDynamique.class)[0];
		rop.doDisconnection();
		
		rop.doConnection(LISTE_REFLECTION_INPORT[3], ReflectionConnector.class.getCanonicalName());
		entite_uri_inport[2] = rop.findInboundPortURIsFromInterface(IOrdinateur.class)[0];
		launch_uri_inport[2] = rop.findInboundPortURIsFromInterface(IEntiteDynamique.class)[0];
		rop.doDisconnection();
		
		rop.doConnection(LISTE_REFLECTION_INPORT[4], ReflectionConnector.class.getCanonicalName());
		entite_uri_inport[3] = rop.findInboundPortURIsFromInterface(IPanneau.class)[0];
		launch_uri_inport[3] = rop.findInboundPortURIsFromInterface(IEntiteDynamique.class)[0];
		rop.doDisconnection();
		
		rop.doConnection(LISTE_REFLECTION_INPORT[5], ReflectionConnector.class.getCanonicalName());
		entite_uri_inport[4] = rop.findInboundPortURIsFromInterface(IBatterie.class)[0];
		launch_uri_inport[4] = rop.findInboundPortURIsFromInterface(IEntiteDynamique.class)[0];
		rop.doDisconnection();
		
		rop.doConnection(LISTE_REFLECTION_INPORT[6], ReflectionConnector.class.getCanonicalName());
		entite_uri_inport[5] = rop.findInboundPortURIsFromInterface(ICompteurControleur.class)[0];
		rop.doDisconnection();
		
		// Connexion du controleur vers les entites
		
		rop.doConnection(LISTE_REFLECTION_INPORT[0], ReflectionConnector.class.getCanonicalName());
		
		rop.toggleTracing();
		rop.toggleLogging();
		
		int j = 0;
		rop.doPortConnection(URI.CONTROLEUR_OP_FRIGO_URI.getURI(), entite_uri_inport[j], 
				ControleurFrigoConnector.class.getCanonicalName());
		
		j++;
		rop.doPortConnection(URI.CONTROLEUR_OP_LAVELINGE_URI.getURI(), entite_uri_inport[j], 
				ControleurLaveLingeConnector.class.getCanonicalName());
		
		j++;
		rop.doPortConnection(URI.CONTROLEUR_OP_ORDINATEUR_URI.getURI(), entite_uri_inport[j], 
				ControleurOrdinateurConnector.class.getCanonicalName());

		j++;
		rop.doPortConnection(URI.CONTROLEUR_OP_PANNEAUSOLAIRE_URI.getURI(), entite_uri_inport[j],
				ControleurPanneauSolaireConnector.class.getCanonicalName());

		j++;
		rop.doPortConnection(URI.CONTROLEUR_OP_BATTERIE_URI.getURI(), entite_uri_inport[j],
				ControleurBatterieConnector.class.getCanonicalName());
		
		j++;
		rop.doPortConnection(URI.CONTROLEUR_OP_COMPTEUR_URI.getURI(), entite_uri_inport[j],
				ControleurCompteurConnector.class.getCanonicalName());

		this.doPortDisconnection(rop.getPortURI());
		
		// ******************* Frigo vers compteur *********************
		
		rop.doConnection(LISTE_REFLECTION_INPORT[6], ReflectionConnector.class.getCanonicalName());
		entite_uri_inport = rop.findInboundPortURIsFromInterface(ICompteur.class);
		rop.doDisconnection();

		rop.doConnection(LISTE_REFLECTION_INPORT[1], ReflectionConnector.class.getCanonicalName());

		rop.toggleTracing();
		rop.toggleLogging();

		rop.doPortConnection(URI.FRIGO_COMPTEUR_OP_URI.getURI(), entite_uri_inport[0], 
				AppareilCompteurConnector.class.getCanonicalName());
		this.doPortDisconnection(rop.getPortURI());
		
		// ******************* Lave-Linge vers compteur *********************

		rop.doConnection(LISTE_REFLECTION_INPORT[2], ReflectionConnector.class.getCanonicalName());

		rop.toggleTracing();
		rop.toggleLogging();

		rop.doPortConnection(URI.LAVELINGE_COMPTEUR_OP_URI.getURI(), entite_uri_inport[0], 
				AppareilCompteurConnector.class.getCanonicalName());
		this.doPortDisconnection(rop.getPortURI());
		
		// ******************* Ordinateur vers compteur *********************

		rop.doConnection(LISTE_REFLECTION_INPORT[3], ReflectionConnector.class.getCanonicalName());

		rop.toggleTracing();
		rop.toggleLogging();

		rop.doPortConnection(URI.ORDINATEUR_COMPTEUR_OP_URI.getURI(), entite_uri_inport[0], 
				AppareilCompteurConnector.class.getCanonicalName());
		this.doPortDisconnection(rop.getPortURI());
		
		// ******************* Panneau solaire vers compteur *********************

		rop.doConnection(LISTE_REFLECTION_INPORT[4], ReflectionConnector.class.getCanonicalName());

		rop.toggleTracing();
		rop.toggleLogging();

		rop.doPortConnection(URI.PANNEAUSOLAIRE_COMPTEUR_OP_URI.getURI(), entite_uri_inport[0], 
				UniteCompteurConnector.class.getCanonicalName());

		this.doPortDisconnection(rop.getPortURI());
		
		// ******************* Batterie solaire vers compteur *********************

		rop.doConnection(LISTE_REFLECTION_INPORT[5], ReflectionConnector.class.getCanonicalName());

		rop.toggleTracing();
		rop.toggleLogging();

		rop.doPortConnection(URI.BATTERIE_COMPTEUR_OP_URI.getURI(), entite_uri_inport[0], 
				UniteCompteurConnector.class.getCanonicalName());

		this.doPortDisconnection(rop.getPortURI());

		// ************************** Frigo vers controleur **********************************
		
		rop.doConnection(LISTE_REFLECTION_INPORT[0], ReflectionConnector.class.getCanonicalName());
		entite_uri_inport = rop.findInboundPortURIsFromInterface(IControleur.class);
		rop.doDisconnection();

		rop.doConnection(LISTE_REFLECTION_INPORT[1], ReflectionConnector.class.getCanonicalName());

		rop.toggleTracing();
		rop.toggleLogging();

		rop.doPortConnection(URI.FRIGO_CONTROLEUR_OP_URI.getURI(), entite_uri_inport[0], 
				AppareilControleurConnector.class.getCanonicalName());
		this.doPortDisconnection(rop.getPortURI());
		
		// ************************** Lave-Linge vers controleur **********************************

		rop.doConnection(LISTE_REFLECTION_INPORT[2], ReflectionConnector.class.getCanonicalName());

		rop.toggleTracing();
		rop.toggleLogging();

		rop.doPortConnection(URI.LAVELINGE_CONTROLEUR_OP_URI.getURI(), entite_uri_inport[0], 
				AppareilControleurConnector.class.getCanonicalName());
		this.doPortDisconnection(rop.getPortURI());
		
		// ************************** Ordinateur vers controleur **********************************

		rop.doConnection(LISTE_REFLECTION_INPORT[3], ReflectionConnector.class.getCanonicalName());

		rop.toggleTracing();
		rop.toggleLogging();

		rop.doPortConnection(URI.ORDINATEUR_CONTROLEUR_OP_URI.getURI(), entite_uri_inport[0], 
				AppareilControleurConnector.class.getCanonicalName());
		this.doPortDisconnection(rop.getPortURI());
		
		// ************************** Panneau solaire vers controleur **********************************

		rop.doConnection(LISTE_REFLECTION_INPORT[4], ReflectionConnector.class.getCanonicalName());

		rop.toggleTracing();
		rop.toggleLogging();

		rop.doPortConnection(URI.PANNEAUSOLAIRE_CONTROLEUR_OP_URI.getURI(), entite_uri_inport[0], 
				UniteControleurConnector.class.getCanonicalName());
		this.doPortDisconnection(rop.getPortURI());
		
		// ************************** Batterie vers controleur **********************************

		rop.doConnection(LISTE_REFLECTION_INPORT[5], ReflectionConnector.class.getCanonicalName());

		rop.toggleTracing();
		rop.toggleLogging();

		rop.doPortConnection(URI.BATTERIE_CONTROLEUR_OP_URI.getURI(), entite_uri_inport[0], 
				UniteControleurConnector.class.getCanonicalName());
		this.doPortDisconnection(rop.getPortURI());

		// *************** Lancement des actions depuis l'assembleur ****************
		
		this.logMessage("Ajout des appareils et des unites de production au systeme...");
		
		rop.unpublishPort();
		
		this.runTask(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try {
					((Assembleur) this.getTaskOwner()).launch();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
		
		
	}
	
	public void launch() throws Exception {
		
		AssembleurOutPort AssembleurOutPort = new AssembleurOutPort(this);
		AssembleurOutPort.publishPort();
		
		this.doPortConnection(AssembleurOutPort.getPortURI(), launch_uri_inport[0],
				AssembleurEntiteConnector.class.getCanonicalName());
		AssembleurOutPort.ajoutLogement(URI.FRIGO_URI.getURI());
		this.doPortDisconnection(AssembleurOutPort.getPortURI());

		this.doPortConnection(AssembleurOutPort.getPortURI(), launch_uri_inport[1],
				AssembleurEntiteConnector.class.getCanonicalName());
		AssembleurOutPort.ajoutLogement(URI.LAVELINGE_URI.getURI());
		this.doPortDisconnection(AssembleurOutPort.getPortURI());

		this.doPortConnection(AssembleurOutPort.getPortURI(), launch_uri_inport[2],
				AssembleurEntiteConnector.class.getCanonicalName());
		AssembleurOutPort.ajoutLogement(URI.ORDINATEUR_URI.getURI());
		this.doPortDisconnection(AssembleurOutPort.getPortURI());

		this.doPortConnection(AssembleurOutPort.getPortURI(), launch_uri_inport[3],
				AssembleurEntiteConnector.class.getCanonicalName());
		AssembleurOutPort.ajoutLogement(URI.PANNEAUSOLAIRE_URI.getURI());
		this.doPortDisconnection(AssembleurOutPort.getPortURI());

		this.doPortConnection(AssembleurOutPort.getPortURI(), launch_uri_inport[4],
				AssembleurEntiteConnector.class.getCanonicalName());
		AssembleurOutPort.ajoutLogement(URI.BATTERIE_URI.getURI());
		this.doPortDisconnection(AssembleurOutPort.getPortURI());
		
		AssembleurOutPort.unpublishPort();
		AssembleurOutPort.destroyPort();
		
	}
	
	@Override
	public void finalise() throws Exception {
		if(this.DynamicOutPort.connected()) {
			this.doPortDisconnection(this.DynamicOutPort.getPortURI());
		}
		super.finalise();
	}
	
	@Override
	public void shutdown() throws ComponentShutdownException {
		try {
			this.DynamicOutPort.unpublishPort();
		} catch (Exception e) { e.printStackTrace(); }
		super.shutdown();
	}
	
	@Override
	public void shutdownNow() throws ComponentShutdownException {
		try {
			this.DynamicOutPort.unpublishPort();
		} catch (Exception e) { e.printStackTrace(); }
		super.shutdownNow();
	}
}