package app.components;

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import app.data.Message;
import app.interfaces.IControleur;
import app.ports.ControleurDataInPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataRequiredI;

public class Controleur extends AbstractComponent implements IControleur {

	public Vector<ControleurDataInPort> dataInPorts = new Vector<ControleurDataInPort>();
	protected ConcurrentHashMap<String, Vector<Message>> appareil_messages = new ConcurrentHashMap<>();

	public Controleur(String reflectionInboundPortURI, int nbThreads, int nbSchedulableThreads, int nbAppareil) throws Exception {
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);

		this.addOfferedInterface(IControleur.class);
		this.addOfferedInterface(DataOfferedI.PullI.class) ;
		createDataInPorts(nbAppareil);
		
		this.tracer.setRelativePosition(1, 1);
	}
	
	protected void createDataInPorts(int nbAppareil) throws Exception {
		if(nbAppareil > 0) {
			for (int i = 0; i < nbAppareil; i++) {
				String dataInPortURI = java.util.UUID.randomUUID().toString();
				dataInPorts.add(new ControleurDataInPort(dataInPortURI, this));
				this.addPort(dataInPorts.get(i));
				dataInPorts.get(i).publishPort();
			}
		}
	}

	protected void envoyerMessage(String uri, int numero_appareil) throws Exception {
		Message m = appareil_messages.get(uri).get(0);
		appareil_messages.get(uri).remove(m);
		this.dataInPorts.get(numero_appareil).send(m);
	}
	
	protected void addMessageToMap(String key, Message m) {
		if(!appareil_messages.containsKey(key))
			appareil_messages.put(key, new Vector<Message>());
		appareil_messages.get(key).add(m);
	}

	@Override
	public DataOfferedI.DataI getMessage(String uri) throws Exception {
		Message m = appareil_messages.get(uri).get(0);
		appareil_messages.get(uri).remove(m);
		return m;
	}
	
	@Override
	public DataRequiredI.DataI getEnergie(Message m) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void execute() throws Exception {
		super.execute();
		
		Message m1 = new Message();
		Message m2 = new Message();
		Message m3 = new Message();
		Message m4 = new Message();
		Message m5 = new Message();
		Message m6 = new Message();
		Message m7 = new Message();
		
		m1.setContenu("Consommation stable...");
		addMessageToMap("frigo", m1);
		
		m2.setContenu("allumer");
		addMessageToMap("ordinateur", m2);
		
		m3.setContenu("eteindre");
		addMessageToMap("ordinateur", m3);
		
		m4.setContenu("fridge temperature cible : 5.0");
		addMessageToMap("frigo", m4);
		
		m5.setContenu("set pourcentage : 95");
		addMessageToMap("chargeur", m5);
		
		m6.setContenu("allumer");
		addMessageToMap("panneau", m6);
		
		m7.setContenu("allumer");
		addMessageToMap("batterie", m7);
		
		
		this.runTask(new AbstractTask() {
			public void run() {
				try {
					this.taskOwner.logMessage("Envoi message au frigo : " + m1.getContenu());
					envoyerMessage("frigo", 0);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				try {
					this.taskOwner.logMessage("Envoi message au frigo : " + m4.getContenu());
					envoyerMessage("frigo", 0);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				try {
					this.taskOwner.logMessage("Envoi message a l'ordinateur : " + m2.getContenu());
					envoyerMessage("ordinateur", 1);
					this.taskOwner.logMessage("Envoi message a l'ordinateur : " + m3.getContenu());
					envoyerMessage("ordinateur", 1);
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					this.taskOwner.logMessage("Envoi message au chargeur : " + m5.getContenu());
					envoyerMessage("chargeur", 2);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
//				try {
//					this.taskOwner.logMessage("Envoi message au panneau solaire : " + m6.getContenu());
//					envoyerMessage("panneau", 3);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				
//				try {
//					this.taskOwner.logMessage("Envoi message a la batterie : " + m7.getContenu());
//					envoyerMessage("batterie", 4);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
			}
		});
		
	}
	
	@Override
	public void shutdown() throws ComponentShutdownException {
		super.shutdown();
	}

}
