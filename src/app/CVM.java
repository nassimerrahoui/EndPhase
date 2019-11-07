package app;

import app.components.Chargeur;
import app.components.Controleur;
import app.components.Frigo;
import app.components.Ordinateur;
import app.connectors.AppareilServiceConnector;
import fr.sorbonne_u.components.cvm.AbstractCVM;

public class CVM extends AbstractCVM {
	
	Controleur controleur;
	Frigo frigo;
	Ordinateur ordinateur;
	Chargeur chargeur;
	String controleurURI = "controleur";
	String frigoURI = "frigo";
	String ordinateurURI = "ordi";
	String chargeurURI = "chargeur";

	public CVM() throws Exception {
		super();
	}

	@Override
	public void deploy() throws Exception {
		this.controleur = new Controleur(controleurURI, 1, 0, 3);
		this.frigo = new Frigo(frigoURI, 1, 0);
		this.ordinateur = new Ordinateur(ordinateurURI, 1, 0);
		this.chargeur = new Chargeur(chargeurURI, 1, 0);
		this.addDeployedComponent(controleurURI,controleur);
		this.addDeployedComponent(frigoURI,frigo);
		this.addDeployedComponent(ordinateurURI, ordinateur);
		this.addDeployedComponent(chargeurURI, chargeur);
		this.toggleTracing(controleurURI);
		this.toggleTracing(frigoURI);
		this.toggleTracing(ordinateurURI);
		this.toggleTracing(chargeurURI);

		
		this.doPortConnection(
				controleurURI,
				this.controleur.dataInPorts.get(0).getPortURI(),
				this.frigo.dataOutPort.getPortURI(),
				AppareilServiceConnector.class.getCanonicalName()) ;
		
		this.doPortConnection(
				controleurURI,
				this.controleur.dataInPorts.get(1).getPortURI(),
				this.ordinateur.dataOutPort.getPortURI(),
				AppareilServiceConnector.class.getCanonicalName()) ;
		
		this.doPortConnection(
				controleurURI,
				this.controleur.dataInPorts.get(2).getPortURI(),
				this.chargeur.dataOutPort.getPortURI(),
				AppareilServiceConnector.class.getCanonicalName()) ;
		
		super.deploy();
	}

	public static void main(String[] args) {
		try {
			CVM cvm = new CVM();
			cvm.startStandardLifeCycle(30000L);
			Thread.sleep(5000L);
			System.exit(0);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
