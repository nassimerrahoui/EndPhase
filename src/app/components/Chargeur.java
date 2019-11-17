package app.components;

import java.util.Vector;
import app.data.Message;
import app.interfaces.IChargeur;
import app.ports.AppareilDataInPort;
import app.ports.AppareilDataOutPort;
import app.util.TypeAppareil;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.interfaces.DataOfferedI;

public class Chargeur extends AbstractComponent implements IChargeur {

	public AppareilDataInPort dataInPort;
	public AppareilDataOutPort dataOutPort;
	protected Vector<Message> messages_recu = new Vector<>();

	protected boolean isLoading;
	protected int delai;
	protected int pourcentage;
	protected Double consommation;
	protected TypeAppareil type;

	public Chargeur(String reflectionInboundPortURI, int nbThreads, int nbSchedulableThreads, String dataOutPortURI, TypeAppareil type) throws Exception {
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);

		dataOutPort = new AppareilDataOutPort(dataOutPortURI, this);
		this.addPort(dataOutPort);
		dataOutPort.publishPort();
		
		String dataInPortURI = java.util.UUID.randomUUID().toString();
		dataInPort = new AppareilDataInPort(dataInPortURI, this);
		this.addPort(dataInPort);
		dataInPort.publishPort();
		
		this.tracer.setRelativePosition(1, 1);
		
		isLoading = false;
		delai = 30;
		pourcentage = 0;
		consommation = 100.0;
		this.type = type;
		
		createNewExecutorService("reception", 5, true);
	}

	@Override
	public void recevoirMessage(Message m) throws Exception {
		this.logMessage("Message recu : " + m.getContenu());
		messages_recu.add(m);
		traitementMessage(m);
	}
	
	protected void envoyerMessage(Message m) throws Exception {
		this.dataInPort.send(m);
	}
	
	@Override
	public DataOfferedI.DataI getConsommation() throws Exception {
		Message m = new Message();
		if(isLoading) {
			m.setContenu("- " + consommation.toString());
			m.setAuteur("chargeurURI");
		}else {
			double veille = consommation.doubleValue()/10;
			consommation = veille;
			m.setContenu("- " + consommation.toString());
			m.setAuteur("chargeurURI");
		}	
		return m;
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
							if(delai == 0) {
								isLoading = true;
								consommation = 100.0;
							}
						}
						
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

}
