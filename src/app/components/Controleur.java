package app.components;

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import app.data.Message;
import app.interfaces.IControleur;
import app.ports.ControleurDataInPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.interfaces.DataOfferedI;

public class Controleur extends AbstractComponent implements IControleur {

	public ControleurDataInPort dataInPort;
	protected ConcurrentHashMap<String, Vector<Message>> appareil_messages = new ConcurrentHashMap<>();

	public Controleur(String reflectionInboundPortURI, int nbThreads, int nbSchedulableThreads) throws Exception {
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);

		this.addOfferedInterface(IControleur.class);
		this.addOfferedInterface(DataOfferedI.PullI.class) ;
		String dataInPortURI = java.util.UUID.randomUUID().toString();
		dataInPort = new ControleurDataInPort(dataInPortURI, this);
		this.addPort(dataInPort);
		dataInPort.publishPort();
	}

	protected void envoyerMessage(String uri) throws Exception {
		Message m = appareil_messages.get(uri).get(0);
		appareil_messages.get(uri).remove(m);
		this.dataInPort.send(m);
	}
	
	protected void addMessageToMap(String key, Message m) {
		if(!appareil_messages.containsKey(key))
			appareil_messages.put(key, new Vector<Message>());
		appareil_messages.get(key).add(m);
	}

	@Override
	public DataI getMessage(String uri) throws Exception {
		Message m = appareil_messages.get(uri).get(0);
		appareil_messages.get(uri).remove(m);
		return m;
	}
	
	@Override
	public void execute() throws Exception {
		super.execute();
		this.runTask(new AbstractTask() {

			public void run() {
				try {
					Message m = new Message();
					m.setContenu("Consommation eleve d'energie, eteindre appareil.");
					addMessageToMap("frigo", m);
					envoyerMessage("frigo");
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			
		});
	}
}
