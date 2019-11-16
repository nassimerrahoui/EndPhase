package app.components;

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import app.data.Message;
import app.interfaces.ICompteur;
import app.ports.CompteurDataInPort;
import app.ports.CompteurDataOutPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.interfaces.DataOfferedI;

public class Compteur extends AbstractComponent implements ICompteur {
	
	CompteurDataInPort dataInPort;
	Vector<CompteurDataOutPort> dataOutPorts;
	protected ConcurrentHashMap<String, Double> appareil_consommation = new ConcurrentHashMap<>();
	protected ConcurrentHashMap<String, Double> unite_production = new ConcurrentHashMap<>();

	protected Compteur(String reflectionInboundPortURI, int nbThreads, int nbSchedulableThreads, Vector<CompteurDataOutPort> dataOutPorts) throws Exception {
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);
		createDataOutPorts(dataOutPorts);
	}
	
	protected void createDataOutPorts(Vector<CompteurDataOutPort> dataOutPorts) throws Exception {
		if(dataOutPorts.size() > 0) {
			for (int i = 0; i < dataOutPorts.size(); i++) {
				this.dataOutPorts.add(dataOutPorts.get(i));
				this.addPort(this.dataOutPorts.get(i));
				this.dataOutPorts.get(i).publishPort();
			}
		}
			
	}

	@Override
	public void recevoirMessage(Message m) throws Exception {
		if(m.getContenu().contains("-"))
			appareil_consommation.put(m.getAuteur(), Double.valueOf(m.getContenu().split("\\s")[1]));
		if(m.getContenu().contains("+"))
			unite_production.put(m.getAuteur(), Double.valueOf(m.getContenu().split("\\s")[1]));
	}

	
	/**
	 * Renvoie un message sous cette forme :
	 * unite1 : 50 | unite2 : 150 | - appareil1 : 30 | appareil2 : 70 | appareil3 : 90 | 
	 */
	@Override
	public DataOfferedI.DataI getConsommation() throws Exception {
		Message m = new Message();
		String energie = "";
		
		for (String uri : unite_production.keySet()) 
			energie += uri + " : " + unite_production.get(uri) + " | ";
		
		energie += "- ";
		
		for (String uri : appareil_consommation.keySet()) 
			energie += uri + " : " + unite_production.get(uri) + " | ";
		
		m.setContenu(energie);
		return m;
	}
	
	

}
