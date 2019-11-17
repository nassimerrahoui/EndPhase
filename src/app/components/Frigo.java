package app.components;

import java.util.Vector;
import app.data.Message;
import app.interfaces.IFrigo;
import app.ports.AppareilDataInPort;
import app.ports.AppareilDataOutPort;
import app.util.TypeAppareil;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.interfaces.DataOfferedI;

public class Frigo extends AbstractComponent implements IFrigo {

	public AppareilDataInPort dataInPort;
	public AppareilDataOutPort dataOutPort;
	protected Vector<Message> messages_recu = new Vector<>();
	
	protected Boolean isOn;
	protected Double freezer_temperature;
	protected Double freezer_temperature_cible;
	protected Double fridge_temperature;
	protected Double fridge_temperature_cible;
	protected Double consommation;
	protected TypeAppareil type;
	
	public Frigo(String reflectionInboundPortURI, int nbThreads, int nbSchedulableThreads, String dataOutPortURI, TypeAppareil type) throws Exception {
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);
		
		dataOutPort = new AppareilDataOutPort(dataOutPortURI, this);
		this.addPort(dataOutPort);
		dataOutPort.publishPort();
		
		String dataInPortURI = java.util.UUID.randomUUID().toString();
		dataInPort = new AppareilDataInPort(dataInPortURI, this);
		this.addPort(dataInPort);
		dataInPort.publishPort();
		
		this.tracer.setRelativePosition(1, 1);
		
		isOn = true;
		fridge_temperature = 4.4;
		fridge_temperature_cible = 3.0;
		freezer_temperature = 0.0;
		freezer_temperature_cible = -10.0;
		consommation = 55.0;
		this.type = type;
	}
	
	protected void freezerStabilize() {
		if(freezer_temperature_cible - freezer_temperature > 0.5) {
			freezer_temperature += 0.2;
			consommation--;
		} else if (freezer_temperature_cible - freezer_temperature < -0.5) {
			freezer_temperature -= 0.2;
			consommation++;
		}
	}
	
	protected void fridgeStabilize() {
		if(fridge_temperature_cible - fridge_temperature > 0.5) {
			fridge_temperature += 0.2;
			consommation++;
		} else if (fridge_temperature_cible - fridge_temperature < -0.5) {
			fridge_temperature -= 0.2;
			consommation--;
		}
	}
	
	protected void tick() throws Exception {
		if(isOn) {
			freezerStabilize();
			fridgeStabilize();
		}
		envoyerMessage((Message) getConsommation());
	}
	
	protected void traitementMessage(Message m) {
		switch (m.getContenu()) {
		case "eteindre":
			if(isOn) {
				this.logMessage("Frigo : je m'eteins...");
				isOn = false;
				consommation = 5.0;
			}
			break;

		case "allumer":
			if(!isOn) {
				this.logMessage("Frigo : demarre...");
				isOn = true;
				consommation = 55.0;
			}
		default:
			if(m.getContenu().contains("fridge temperature cible")) {
				fridge_temperature_cible = Double.valueOf(m.getContenu().split("\\s+")[4]);
				this.logMessage("Fridge temperature cible : " + fridge_temperature_cible.toString());
			} else if(m.getContenu().contains("freeze temperature cible")) {
				freezer_temperature_cible = Double.valueOf(m.getContenu().split("\\s+")[4]);
			}
			break;
		}
		
		messages_recu.remove(m);
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
		// 0.2 degre -> 1 Watt
		Message m = new Message();
		m.setContenu("- "+consommation.toString());
		m.setAuteur("frigoURI");
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
					while(isOn) {
						Thread.sleep(2000);
						tick();
						this.taskOwner.logMessage("fridge : " + (Math.round(fridge_temperature*100.0)/100.0) + " �C");
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
