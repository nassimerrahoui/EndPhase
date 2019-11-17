package app.components;

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import app.data.Message;
import app.interfaces.IControleur;
import app.ports.ControleurDataInPort;
import app.ports.ControleurDataOutPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
public class Controleur extends AbstractComponent implements IControleur {

	public ControleurDataOutPort dataOutPort;
	public ConcurrentHashMap<String, ControleurDataInPort> dataInPorts = new ConcurrentHashMap<>();
	protected ConcurrentHashMap<String, Vector<Message>> appareil_messages = new ConcurrentHashMap<>();
	protected ConcurrentHashMap<String, Double> appareil_consommation = new ConcurrentHashMap<>();
	protected ConcurrentHashMap<String, Double> unite_production = new ConcurrentHashMap<>();
	
	protected Vector<String[]> priorites = new Vector<String[]>();
	protected Vector<String[]> uproductions = new Vector<String[]>();
	
	protected boolean allume_appareil_permanent;
	protected boolean batterie;

	public Controleur(String reflectionInboundPortURI, int nbThreads, int nbSchedulableThreads, String dataOutPortURI,
			Vector<String[]> priorites,
			Vector<String[]> uproductions) throws Exception {
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);

		this.addOfferedInterface(IControleur.class);
		this.addOfferedInterface(DataOfferedI.PullI.class);

		dataOutPort = new ControleurDataOutPort(dataOutPortURI, this);
		this.addPort(dataOutPort);
		dataOutPort.publishPort();

		this.priorites = priorites;
		this.uproductions = uproductions;
		
		this.allume_appareil_permanent = false;
		this.batterie = false;
		
		createDataInPorts();

		this.tracer.setRelativePosition(1, 0);
	}

	protected void createDataInPorts() throws Exception {
		if (priorites.size() > 0) {
			for (int i = 0; i < priorites.size(); i++) {
				String dataInPortURI = java.util.UUID.randomUUID().toString();
				dataInPorts.put(priorites.get(i)[0], new ControleurDataInPort(dataInPortURI, this));
				this.addPort(dataInPorts.get(priorites.get(i)[0]));
				dataInPorts.get(priorites.get(i)[0]).publishPort();
			}
		}
		if (uproductions.size() > 0) {
			for (int i = 0; i < uproductions.size(); i++) {
				String dataInPortURI = java.util.UUID.randomUUID().toString();
				dataInPorts.put(uproductions.get(i)[0], new ControleurDataInPort(dataInPortURI, this));
				this.addPort(dataInPorts.get(uproductions.get(i)[0]));
				dataInPorts.get(uproductions.get(i)[0]).publishPort();
			}
		}
	}

	protected void envoyerMessage(String uri) throws Exception {
		if(appareil_messages.get(uri).size() > 0) {
			Message m = appareil_messages.get(uri).get(0);
			appareil_messages.get(uri).remove(m);
			this.dataInPorts.get(uri).send(m);
		}
	}

	protected void addMessageToMap(String key, Message m) {
		if (!appareil_messages.containsKey(key))
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
	public void getEnergie(Message m) throws Exception {
		this.logMessage(m.getContenu());
		
		String partieProdu = m.getContenu().split("-")[0];
		String partieConso = m.getContenu().split("-")[1];

		for (String unite : partieProdu.split("|"))
			unite_production.put(unite.split("\\s")[0], Double.valueOf(unite.split("\\s")[2]));

		for (String appareil : partieConso.split("|"))
			appareil_consommation.put(appareil.split("\\s")[0], Double.valueOf(appareil.split("\\s")[2]));
	}

	protected double getProduction() {
		double energie_produite = 0.0;

		for (String uri : unite_production.keySet())
			energie_produite += unite_production.get(uri);

		return energie_produite;
	}

	protected double getConsommation() {
		double energie_consommee = 0.0;

		for (String uri : appareil_consommation.keySet())
			energie_consommee += appareil_consommation.get(uri);

		return energie_consommee;
	}

	protected double getConsommation(int fin) {
		double energie_consommee = 0.0;

		int i = 0;
		for (String uri : appareil_consommation.keySet()) {
			if (i == fin)
				break;

			energie_consommee += appareil_consommation.get(uri);
			i++;
		}

		return energie_consommee;
	}

	protected void make_decisions() throws Exception {
		if (getConsommation() <= getProduction() && !allume_appareil_permanent) {
			System.out.println("ON PEUT ALLUMER UN APPAREIL");
			allume_appareil_permanent = true;
			for (int i = 0; i < priorites.size(); i++) {
				if (!priorites.get(i)[1].equals("1"))
					break;
				Message m = new Message();
				m.setContenu("allumer");
				m.setAuteur("controleurURI");
				addMessageToMap(priorites.get(i)[0], m);
				envoyerMessage(priorites.get(i)[0]);
			}

		} else if (getConsommation() > getProduction()) {
			System.out.println("ON DOIT ALLUMER LA BATTERIE");
			allume_appareil_permanent = false;
			if (!batterie) {
				Message b = new Message();
				b.setContenu("allumer");
				b.setAuteur("controleurURI");
				addMessageToMap("batterieURI", b);
				envoyerMessage("batterieURI");
				batterie = true;
			} else {
				System.out.println("ON DOIT ETEINDRE UN APPAREIL");
				for (int i = priorites.size() - 1; i > 0; i--) {
					if (getConsommation(i) > getProduction()) {
						Message m = new Message();
						m.setContenu("eteindre");
						m.setAuteur("controleurURI");
						addMessageToMap(priorites.get(i)[0], m);
						envoyerMessage(priorites.get(i)[0]);
					}
				}
			}
		}else{
			System.out.println("RIP");
		}
	}

	@Override
	public void start() throws ComponentStartException {
		super.start();
		
		this.runTask(new AbstractTask() {
			public void run() {
				
				Message m1 = new Message();
				Message m2 = new Message();
				Message m3 = new Message();
				Message m4 = new Message();

				m1.setContenu("allumer");
				m1.setAuteur("controleurURI");
				addMessageToMap("panneauURI", m1);

				m2.setContenu("allumer");
				m2.setAuteur("controleurURI");
				addMessageToMap("frigoURI", m2);

				m3.setContenu("allumer");
				m3.setAuteur("controleurURI");
				addMessageToMap("ordinateurURI", m3);

				m4.setContenu("allumer : 50");
				m4.setAuteur("controleurURI");
				addMessageToMap("chargeurURI", m4);
				
				try {
					envoyerMessage("panneauURI");
					envoyerMessage("frigoURI");
					envoyerMessage("ordinateurURI");
					envoyerMessage("chargeurURI");
				} catch (Exception e) {
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
				
				Message m4 = new Message();
				Message m5 = new Message();

				m4.setContenu("fridge temperature cible : 3.0");
				m4.setAuteur("controleurURI");
				addMessageToMap("frigoURI", m4);

				m5.setContenu("set pourcentage : 95");
				m5.setAuteur("controleurURI");
				addMessageToMap("chargeurURI", m5);

				try {
					this.taskOwner.logMessage("Envoi message au frigo : " + m4.getContenu());
					envoyerMessage("frigoURI");
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					this.taskOwner.logMessage("Envoi message au chargeur : " + m5.getContenu());
					envoyerMessage("chargeurURI");
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				while (true) {
					try {
						make_decisions();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	@Override
	public void shutdown() throws ComponentShutdownException {
		super.shutdown();
	}

}
