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
		appareil_consommation.put(m.getAuteur(), Double.valueOf(m.getContenu()));
	}

	@Override
	public DataOfferedI.DataI getConsommation() throws Exception {
		return null;
	}
	
	

}
