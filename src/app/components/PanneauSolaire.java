package app.components;

import java.util.Vector;
import app.data.Message;
import app.interfaces.IAppareil;
import app.interfaces.IProduction;
import app.interfaces.IUProduction;
import app.interfaces.IUniteProduction;
import app.ports.UProductionDataInPort;
import app.ports.UProductionDataOutPort;
import app.util.EtatAppareil;
import app.util.EtatUniteProduction;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.interfaces.DataOfferedI;

public class PanneauSolaire extends AbstractComponent implements IUniteProduction, IProduction {

	public UProductionDataInPort dataInPort;
	public UProductionDataOutPort dataOutPort;
	Vector<Message> messages_recu = new Vector<>();
	protected boolean isOn;
	protected Double production;
	
	public PanneauSolaire(String reflectionInboundPortURI, int nbThreads, int nbSchedulableThreads, String dataOutPortURI) throws Exception {
		
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);

		dataOutPort = new UProductionDataOutPort(dataOutPortURI, this);
		this.addPort(dataOutPort);
		dataOutPort.publishPort();
		
		String dataInPortURI = java.util.UUID.randomUUID().toString();
		dataInPort = new UProductionDataInPort(dataInPortURI, this);
		this.addPort(dataInPort);
		dataInPort.publishPort();
		
		isOn = false;
		production = 0.0;
		
		this.tracer.setRelativePosition(1, 2);
	}
	
	@Override
	public double getProduction() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public void setEtatUProduction(EtatUniteProduction etat) throws Exception {
		// TODO Auto-generated method stub
		
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
				try {
					while(true){
						Thread.sleep(1000);
						this.taskOwner.logMessage(" Envoi message au compteur : " + ((Message) getProduction()).getContenu());
						envoyerMessage((Message) getProduction());
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
		try {
			this.dataOutPort.unpublishPort();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	

}
