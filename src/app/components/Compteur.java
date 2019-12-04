package app.components;

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import app.data.Message;
import app.interfaces.ICompteur;
import app.ports.CompteurDataInPort;
import app.ports.CompteurDataOutPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.interfaces.DataOfferedI;

public class Compteur extends AbstractComponent implements ICompteur {

	public CompteurDataInPort dataInPort;
	Vector<CompteurDataOutPort> dataOutPorts = new Vector<>();
	protected ConcurrentHashMap<String, Double> appareil_consommation = new ConcurrentHashMap<>();
	protected ConcurrentHashMap<String, Double> unite_production = new ConcurrentHashMap<>();

	public Compteur(String reflectionInboundPortURI, int nbThreads, int nbSchedulableThreads,
			Vector<String> dataOutPorts) throws Exception {
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);
		createDataOutPorts(dataOutPorts);

		String dataInPortURI = java.util.UUID.randomUUID().toString();
		dataInPort = new CompteurDataInPort(dataInPortURI, this);
		this.addPort(dataInPort);
		dataInPort.publishPort();
		
		this.tracer.setRelativePosition(1, 3);
	}

	protected void createDataOutPorts(Vector<String> dataOutPorts) throws Exception {
		for (int i = 0; i < dataOutPorts.size(); i++) {
			this.dataOutPorts.add(new CompteurDataOutPort(dataOutPorts.get(i), this));
			this.addPort(this.dataOutPorts.get(i));
			this.dataOutPorts.get(i).publishPort();
		}
	}

	protected void envoyerMessage(Message m) throws Exception {
		this.dataInPort.send(m);
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
					while (true) {
						Thread.sleep(4000);

					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				} catch (Exception e) {
					//e.printStackTrace();
				}
			}

		});
	}
	
	@Override
	public void shutdown() throws ComponentShutdownException {
		super.shutdown();
	}

	@Override
	public void envoyerConsommation(double consommation) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getConsommation() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

}
