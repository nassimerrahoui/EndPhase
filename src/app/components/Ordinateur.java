package app.components;

import app.interfaces.IConsommation;
import app.interfaces.IOrdinateur;
import app.ports.frigo.FrigoConsoInPort;
import app.ports.ordi.OrdinateurInPort;
import app.util.EtatAppareil;
import app.util.ModeOrdinateur;
import app.util.TypeAppareil;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;

public class Ordinateur extends AbstractComponent implements IOrdinateur, IConsommation {
	
	protected OrdinateurInPort controleur_INPORT;
	protected FrigoConsoInPort compteur_INPORT;

	protected TypeAppareil type;
	protected EtatAppareil etat;
	protected ModeOrdinateur mode;
	
	protected Double consommation;
	
	
	public Ordinateur(String reflectionInboundPortURI, int nbThreads, int nbSchedulableThreads, String dataOutPortURI, TypeAppareil type) throws Exception {
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);

//		compteur_INPORT = new OrdinateurInPort(dataOutPortURI, this);
//		this.addPort(compteur_INPORT);
//		compteur_INPORT.publishPort();
//		
//		String dataInPortURI = java.util.UUID.randomUUID().toString();
//		controleur_INPORT = new AppareilDataInPort(dataInPortURI, this);
//		this.addPort(controleur_INPORT);
//		controleur_INPORT.publishPort();
		
		etat = EtatAppareil.ON;
		consommation = 90.0;
		this.type = type;
		
		this.tracer.setRelativePosition(2, 1);
		
	}
	
	
	@Override
	public void setEtatAppareil(EtatAppareil etat) throws Exception {
		// TODO Auto-generated method stub
		
	}


	@Override
	public double getConsommation() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public void setMode(ModeOrdinateur mo) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	
	
	@Override
	public void start() throws ComponentStartException {
		super.start();
		this.runTask(new AbstractTask() {

			public void run() {
				try {
					Thread.sleep(2500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		});
	}
	
	@Override
	public void execute() throws Exception {
		super.execute();
		
		this.runTask(new AbstractTask() {

			public void run() {
				try {
					while(true) {
						Thread.sleep(1000);
						//envoyerMessage((Message) getConsommation());
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				} catch (Exception e) {
					//e.printStackTrace();
				}
			}
			
		});
	}

	@Override
	public void shutdown() throws ComponentShutdownException {
		super.shutdown();
		try {
			this.controleur_INPORT.unpublishPort();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	

}
