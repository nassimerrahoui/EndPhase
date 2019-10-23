package app.components;

import java.util.Vector;
import app.data.Message;
import app.interfaces.IFrigo;
import app.ports.AppareilDataOutPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;

public class Frigo extends AbstractComponent implements IFrigo {

	public AppareilDataOutPort dataOutPort;
	Vector<Message> messages_recu = new Vector<>();
	
	protected Boolean isOn;
	protected Double freezer_temperature;
	protected Double freezer_temperature_cible;
	protected Double fridge_temperature;
	protected Double fridge_temperature_cible;
	
	public Frigo(String reflectionInboundPortURI, int nbThreads, int nbSchedulableThreads) throws Exception {
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);
		String dataOutPortURI = java.util.UUID.randomUUID().toString();
		dataOutPort = new AppareilDataOutPort(dataOutPortURI, this);
		this.addPort(dataOutPort);
		dataOutPort.publishPort();
		isOn = true;
		fridge_temperature = 4.4;
		fridge_temperature_cible = 3.0;
		freezer_temperature = 0.0;
		freezer_temperature_cible = -10.0;
	}
	
	protected void freezerStabilize() {
		if(freezer_temperature_cible - freezer_temperature > 0.5) {
			freezer_temperature += 0.2;
		} else if (freezer_temperature_cible - freezer_temperature < -0.5) {
			freezer_temperature -= 0.2;
		}
	}
	
	protected void fridgeStabilize() {
		if(fridge_temperature_cible - fridge_temperature > 0.5) {
			fridge_temperature += 0.2;
		} else if (fridge_temperature_cible - fridge_temperature < -0.5) {
			fridge_temperature -= 0.2;
		}
	}
	
	/** TODO **/
	protected void tick() {
		if(isOn) {
			freezerStabilize();
			fridgeStabilize();
		}
	}
	
	protected void traitementMessage(Message m) {
		switch (m.getContenu()) {
		case "eteindre":
			if(isOn) {
				this.logMessage("Frigo : je m'eteins...");
				isOn = false;
			}
			break;

		case "allumer":
			if(!isOn) {
				this.logMessage("Frigo : demarre...");
				isOn = true;
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
						this.taskOwner.logMessage("fridge : " + (Math.round(fridge_temperature*100.0)/100.0) + " °C");
					}
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
