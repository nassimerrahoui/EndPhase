package app.components;

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import app.data.Message;
import app.interfaces.IControleur;
import app.ports.ControleurDataInPort;
import fr.sorbonne_u.components.AbstractComponent;

public class Controleur extends AbstractComponent implements IControleur {
	
	ControleurDataInPort dataInPort;
	ConcurrentHashMap<String, Vector<Message>> appareil_messages = new ConcurrentHashMap<>();

	protected Controleur(int nbThreads, int nbSchedulableThreads) {
		super(nbThreads, nbSchedulableThreads);
		
		String frigoPortURI = java.util.UUID.randomUUID().toString();
		try {
			dataInPort = new ControleurDataInPort(frigoPortURI, this.getInterface(Controleur.class),this);
			this.addPort(dataInPort);
			dataInPort.publishPort();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	protected void envoyerMessage(String uri) throws Exception {
		Message m = appareil_messages.get(uri).get(0);
		appareil_messages.get(uri).remove(m);
		this.dataInPort.send(m);
	}
}
