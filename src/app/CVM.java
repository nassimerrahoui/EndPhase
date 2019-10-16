package app;

import app.components.Controleur;
import app.components.Frigo;
import app.connectors.AppareilServiceConnector;
import fr.sorbonne_u.components.cvm.AbstractCVM;

public class CVM extends AbstractCVM {
	
	Controleur controleur;
	Frigo frigo;
	String controleurURI = "controleur";
	String frigoURI = "frigo";

	public CVM() throws Exception {
		super();
	}

	@Override
	public void deploy() throws Exception {
		this.controleur = new Controleur(controleurURI, 1, 0);
		this.frigo = new Frigo(frigoURI, 1, 0);
		this.addDeployedComponent(controleurURI,controleur);
		this.addDeployedComponent(frigoURI,frigo);
		this.toggleTracing(controleurURI);
		this.toggleTracing(frigoURI);
		
		this.doPortConnection(
				controleurURI,
				this.controleur.dataInPort.getPortURI(),
				this.frigo.dataOutPort.getPortURI(),
				AppareilServiceConnector.class.getCanonicalName()) ;
		
		super.deploy();
	}

	public static void main(String[] args) {
		try {
			CVM cvm = new CVM();
			cvm.startStandardLifeCycle(10000L);
			Thread.sleep(5000L);
			System.exit(0);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
// -----------------------------------------------------------------------------
