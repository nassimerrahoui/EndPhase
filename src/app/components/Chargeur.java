package app.components;

import java.util.Vector;
import app.data.Message;
import app.interfaces.IChargeur;
import app.ports.AppareilDataOutPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentStartException;

public class Chargeur extends AbstractComponent implements IChargeur {

	public AppareilDataOutPort dataOutPort;
	protected Vector<Message> messages_recu = new Vector<>();

	protected boolean isLoading;
	protected int delai;
	protected int pourcentage;

	public Chargeur(String reflectionInboundPortURI, int nbThreads, int nbSchedulableThreads) throws Exception {
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);

		String dataOutPortURI = java.util.UUID.randomUUID().toString();
		dataOutPort = new AppareilDataOutPort(dataOutPortURI, this);
		this.addPort(dataOutPort);
		dataOutPort.publishPort();
		
		this.tracer.setRelativePosition(1, 1);
		
		isLoading = false;
		delai = 30;
		pourcentage = 0;
		
		createNewExecutorService("reception", 5, true);
	}

	@Override
	public void recevoirMessage(Message m) throws Exception {
		this.logMessage("Message recu : " + m.getContenu());
		messages_recu.add(m);
		traitementMessage(m);
	}

	protected void traitementMessage(Message m) {
		switch (m.getContenu()) {
		case "eteindre":
			if(isLoading) {
				this.logMessage("Chargeur : je m'eteins...");
				isLoading = false;
			}
		
		default:
			if(m.getContenu().contains("allumer")) {
				delai = Integer.valueOf(m.getContenu().split("\\s+")[2]);
			} else if(m.getContenu().contains("set")) {
				pourcentage = Integer.valueOf(m.getContenu().split("\\s+")[3]);
			}
			break;
		}
	}
	
	protected void rechargement() throws InterruptedException {
		if(pourcentage < 100) {
			Thread.sleep(3000);
			pourcentage++;
			this.logMessage("Chargeur : " + pourcentage + " %...");
		} else {
			isLoading = false;
			delai = 30;
		}
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
							rechargement();
						} else {
							delai--;
							if(delai == 0)
								isLoading = true;
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		});
	}

}
