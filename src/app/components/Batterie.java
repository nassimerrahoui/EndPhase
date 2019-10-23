package app.components;

import java.util.Vector;
import app.data.Message;
import app.interfaces.IUProduction;
import app.ports.UProductionDataOutPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;

public class Batterie extends AbstractComponent implements IUProduction {

	public UProductionDataOutPort dataOutPort;
	Vector<Message> messages_recu = new Vector<>();
	protected boolean isOn;
	
	public Batterie(String reflectionInboundPortURI, int nbThreads, int nbSchedulableThreads) throws Exception {
		
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);
		String dataOutPortURI = java.util.UUID.randomUUID().toString();
		dataOutPort = new UProductionDataOutPort(dataOutPortURI, this);
		this.addPort(dataOutPort);
		dataOutPort.publishPort();
		isOn = false;
	}
	
	@Override
	public void recevoirMessage(Message m) throws Exception {
		this.logMessage("Message recu : " + m.getContenu());
		if(isOn) {
			messages_recu.add(m);
			if(m.getContenu().equals("eteindre")) {
				this.logMessage("Batterie : je m'eteins...");
				isOn = false;
			}
			
			messages_recu.remove(m);
			
		} else if (m.getContenu().equals("allumer")) {
			this.logMessage("Batterie : demarre...");
			isOn = true;
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
	public void shutdown() throws ComponentShutdownException {
		super.shutdown();
		try {
			this.dataOutPort.unpublishPort();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
