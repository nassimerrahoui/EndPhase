package app.components;

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import app.interfaces.appareil.IConsommation;
import app.interfaces.appareil.IFrigo;
import app.interfaces.controleur.ICompteur;
import app.interfaces.controleur.IControleFrigo;
import app.interfaces.controleur.IControleLaveLinge;
import app.interfaces.controleur.IControleOrdinateur;
import app.interfaces.controleur.IControleur;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.interfaces.DataOfferedI;

@OfferedInterfaces(offered = { IFrigo.class, IConsommation.class })
@RequiredInterfaces(required = { ICompteur.class })
public class Controleur extends AbstractComponent 
implements IControleur, IControleFrigo, IControleLaveLinge, IControleOrdinateur {


	protected ConcurrentHashMap<String, Double> unite_production = new ConcurrentHashMap<>();
	
	protected Vector<String[]> priorites = new Vector<String[]>();
	protected Vector<String[]> uproductions = new Vector<String[]>();
	
	protected boolean allume_appareil_permanent;
	protected boolean eteindre_appareil;
	protected boolean batterie;

	public Controleur(String reflectionInboundPortURI, int nbThreads, int nbSchedulableThreads, String dataOutPortURI,
			Vector<String[]> priorites,
			Vector<String[]> uproductions) throws Exception {
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);

		this.addOfferedInterface(IControleur.class);
		this.addOfferedInterface(DataOfferedI.PullI.class);

		dataOutPort = new ControleurDataOutPort(dataOutPortURI, this);
		this.addPort(dataOutPort);
		dataOutPort.publishPort();

		this.priorites = priorites;
		this.uproductions = uproductions;
		
		this.allume_appareil_permanent = false;
		this.eteindre_appareil = false;
		this.batterie = false;
		
		createDataInPorts();

		this.tracer.setRelativePosition(1, 0);
	}

	protected double getProduction() {
		double energie_produite = 0.0;

		for (String uri : unite_production.keySet())
			energie_produite += unite_production.get(uri);

		return energie_produite;
	}

	protected double getConsommation() {
		double energie_consommee = 0.0;

		for (String uri : appareil_consommation.keySet()) {
			energie_consommee += appareil_consommation.get(uri);
		}

		return energie_consommee;
	}

	protected double getConsommation(int fin) {
		double energie_consommee = 0.0;

		int i = 0;
		for (String uri : appareil_consommation.keySet()) {
			if (i == fin)
				break;

			energie_consommee += appareil_consommation.get(uri);
			i++;
		}

		return energie_consommee;
	}
}
