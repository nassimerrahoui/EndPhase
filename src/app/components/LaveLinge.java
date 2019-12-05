package app.components;

import app.interfaces.IConsommation;
import app.interfaces.ILaveLinge;
import app.ports.LaveLingeInPort;
import app.ports.frigo.FrigoConsoInPort;
import app.util.EtatAppareil;
import app.util.ModeLaveLinge;
import app.util.TemperatureLaveLinge;
import app.util.TypeAppareil;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentStartException;

public class LaveLinge extends AbstractComponent implements ILaveLinge, IConsommation {

	protected LaveLingeInPort controleur_INPORT;
	protected FrigoConsoInPort compteur_INPORT;

	protected TypeAppareil type;
	protected EtatAppareil etat;
	protected ModeLaveLinge mode;
	
	protected int delai;
	protected Double consommation;
	

	public LaveLinge(String reflectionInboundPortURI, int nbThreads, int nbSchedulableThreads, String dataOutPortURI, TypeAppareil type) throws Exception {
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);

		compteur_INPORT = new FrigoConsoInPort(dataOutPortURI, this);
		this.addPort(compteur_INPORT);
		compteur_INPORT.publishPort();
		
		String dataInPortURI = java.util.UUID.randomUUID().toString();
		//controleur_INPORT = new Conto(dataInPortURI, this);
		this.addPort(controleur_INPORT);
		controleur_INPORT.publishPort();
		
		this.tracer.setRelativePosition(1, 1);
		
		delai = 30;
		consommation = 100.0;
		this.type = type;
		
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
	public void planifierCycle(double heure) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void planifierMode(ModeLaveLinge ml, double heure) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTemperature(TemperatureLaveLinge tl) {
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
						if(delai == 0) {
							//rechargement();
						} else {
							delai--;
							if(delai == 0) {
								//isLoading = true;
								consommation = 100.0;
							}
						}
						
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




	

}
