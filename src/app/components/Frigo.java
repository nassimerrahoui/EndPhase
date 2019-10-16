package app.components;

import java.util.Vector;

import app.data.Message;
import app.interfaces.IFrigo;
import app.ports.AppareilDataOutPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentStartException;

public class Frigo extends AbstractComponent implements IFrigo {

	public AppareilDataOutPort dataOutPort;
	Vector<Message> messages_recu = new Vector<>();
	
	Boolean isOn;
	Double freezer_temperature;
	Double freezer_temperature_cible;
	Double fridge_temperature;
	Double fridge_temperature_cible;
	
	public Frigo(String reflectionInboundPortURI, int nbThreads, int nbSchedulableThreads) throws Exception {
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);
		String dataOutPortURI = java.util.UUID.randomUUID().toString();
		dataOutPort = new AppareilDataOutPort(dataOutPortURI, this);
		this.addPort(dataOutPort);
		dataOutPort.publishPort();
	}
	
	protected void freezerStabilize() {
		if(freezer_temperature_cible - freezer_temperature > 2) {
			freezer_temperature += 0.2;
		} else if (freezer_temperature_cible - freezer_temperature < -2) {
			freezer_temperature -= 0.2;
		}
	}
	
	protected void fridgeStabilize() {
		if(fridge_temperature_cible - fridge_temperature > 2) {
			fridge_temperature += 0.2;
		} else if (fridge_temperature_cible - fridge_temperature < -2) {
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

	@Override
	public void recevoirMessage(Message m) throws Exception {
		messages_recu.add(m);
		this.logMessage("Frigo : " + messages_recu.get(0).getContenu());
	}
	
	@Override
	public void start() throws ComponentStartException {
		super.start();
		this.runTask(new AbstractTask() {

			public void run() {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		});
	}
}
