package app.components;

import java.util.Vector;
import app.data.Message;
import app.interfaces.IOrdinateur;
import app.ports.AppareilDataInPort;
import app.ports.AppareilDataOutPort;
import app.util.TypeAppareil;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.interfaces.DataOfferedI;

public class Ordinateur extends AbstractComponent implements IOrdinateur {
	
	public AppareilDataInPort dataInPort;
	public AppareilDataOutPort dataOutPort;
	Vector<Message> messages_recu = new Vector<>();
	protected boolean isOn;
	protected Double consommation;
	protected TypeAppareil type;
	
	public Ordinateur(String reflectionInboundPortURI, int nbThreads, int nbSchedulableThreads, String dataOutPortURI, TypeAppareil type) throws Exception {
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);

		dataOutPort = new AppareilDataOutPort(dataOutPortURI, this);
		this.addPort(dataOutPort);
		dataOutPort.publishPort();
		
		String dataInPortURI = java.util.UUID.randomUUID().toString();
		dataInPort = new AppareilDataInPort(dataInPortURI, this);
		this.addPort(dataInPort);
		dataInPort.publishPort();
		
		isOn = false;
		consommation = 90.0;
		this.type = type;
		
		this.tracer.setRelativePosition(2, 1);
		
	}
	
	@Override
	public void recevoirMessage(Message m) throws Exception {
		this.logMessage("Message recu : " + m.getContenu());
		if(isOn) {
			messages_recu.add(m);
			if(m.getContenu().equals("eteindre")) {
				this.logMessage("Ordinateur : je m'eteins...");
				isOn = false;
			}
			
			messages_recu.remove(m);
			
		} else if (m.getContenu().equals("allumer")) {
			this.logMessage("Ordinateur : Je demarre...");
			isOn = true;
			consommation = 90.0;
		}
	}
	
	protected void envoyerMessage(Message m) throws Exception {
		this.dataInPort.send(m);
	}
	
	@Override
	public DataOfferedI.DataI getConsommation() throws Exception {
		Message m = new Message();
		if(isOn) {
			m.setContenu("- " + consommation.toString());
			m.setAuteur("ordinateurURI");
		}else {
			double veille = consommation.doubleValue()/3;
			consommation = veille;
			m.setContenu("- " + consommation.toString());
			m.setAuteur("ordinateurURI");
		}	
		
		return m;
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
						envoyerMessage((Message) getConsommation());
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (Exception e) {
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
