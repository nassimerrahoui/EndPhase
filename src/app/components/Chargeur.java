package app.components;

import java.util.Vector;

import app.data.Message;
import app.interfaces.IChargeur;
import app.ports.AppareilDataOutPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractComponent.AbstractTask;

public class Chargeur extends AbstractComponent implements IChargeur {

	protected AppareilDataOutPort dataOutPort;
	protected Vector<Message> messages_recu = new Vector<>();
	
	protected boolean isLoading;
	protected int delai;
	protected int pourcentage;

	protected Chargeur(String reflectionInboundPortURI, int nbThreads, int nbSchedulableThreads) throws Exception {
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);

		String dataOutPortURI = java.util.UUID.randomUUID().toString();
		dataOutPort = new AppareilDataOutPort(dataOutPortURI, this);
		this.addPort(dataOutPort);
		dataOutPort.publishPort();
	}

	@Override
	public void recevoirMessage(Message m) throws Exception {
		this.logMessage("Message recu : " + m.getContenu());
		messages_recu.add(m);
		traitementMessage(m);
	}

	protected void traitementMessage(Message m) {
		
	}
	
	protected void rechargement() throws InterruptedException {
		if(pourcentage < 100) {
			Thread.sleep(3000);
			pourcentage++;
		} else {
			isLoading = false;
			delai = 30;
		}
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
							this.taskOwner.logMessage("Chargeur : " + pourcentage + " %...");
				
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
