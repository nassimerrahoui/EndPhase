package app.components;

import java.util.Vector;
import app.data.Message;
import app.interfaces.IUProduction;
import app.ports.UProductionDataInPort;
import app.ports.UProductionDataOutPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.interfaces.DataOfferedI;

public class PanneauSolaire extends AbstractComponent implements IUProduction {

	public UProductionDataInPort dataInPort;
	public UProductionDataOutPort dataOutPort;
	Vector<Message> messages_recu = new Vector<>();
	protected boolean isOn;
	protected Double production;
	
	public PanneauSolaire(String reflectionInboundPortURI, int nbThreads, int nbSchedulableThreads, String dataOutPortURI) throws Exception {
		
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);

		dataOutPort = new UProductionDataOutPort(dataOutPortURI, this);
		this.addPort(dataOutPort);
		dataOutPort.publishPort();
		
		String dataInPortURI = java.util.UUID.randomUUID().toString();
		dataInPort = new UProductionDataInPort(dataInPortURI, this);
		this.addPort(dataInPort);
		dataInPort.publishPort();
		
		isOn = false;
		production = 0.0;
		
		this.tracer.setRelativePosition(1, 2);
	}
	
	@Override
	public void recevoirMessage(Message m) throws Exception {
		this.logMessage("Message recu : " + m.getContenu());
		if(isOn) {
			messages_recu.add(m);
			if(m.getContenu().equals("eteindre")) {
				this.logMessage("Panneau Solaire : je m'eteins...");
				isOn = false;
				production = 0.0;
			}
			
			messages_recu.remove(m);
			
		} else if (m.getContenu().equals("allumer")) {
			this.logMessage("Panneau Solaire : demarre...");
			isOn = true;
			production = 150.0;
		}
	}
	
	@Override
	public DataOfferedI.DataI getProduction() throws Exception {
		Message m = new Message();
		m.setContenu("+ " + production.toString());
		m.setAuteur("panneauURI");
		return m;
	}
	
	protected void envoyerMessage(Message m) throws Exception {
		this.dataInPort.send(m);
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
				} catch (Exception e) {
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
					while(true){
						Thread.sleep(1000);
						this.taskOwner.logMessage(" Envoi message au compteur : " + ((Message) getProduction()).getContenu());
						envoyerMessage((Message) getProduction());
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
			this.dataOutPort.unpublishPort();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
