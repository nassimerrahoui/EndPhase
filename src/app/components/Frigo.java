package app.components;

import app.interfaces.IConsommation;
import app.interfaces.IFrigo;
import app.ports.CompteurInPort;
import app.ports.FrigoInPort;
import app.util.EtatAppareil;
import app.util.ModeFrigo;
import app.util.TypeAppareil;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;


public class Frigo extends AbstractComponent implements IFrigo, IConsommation {

	protected FrigoInPort controleur_INPORT;
	protected CompteurInPort compteur_INPORT;
	protected TypeAppareil type;
	
	protected EtatAppareil etat;
	protected ModeFrigo lumiere_refrigerateur;
	protected ModeFrigo lumiere_congelateur;
	
	protected Double freezer_temperature;
	protected Double freezer_temperature_cible;
	protected Double fridge_temperature;
	protected Double fridge_temperature_cible;
	protected Double consommation;
	
	
	protected Frigo(String reflectionInboundPortURI, int nbThreads, int nbSchedulableThreads, String dataOutPortURI, TypeAppareil type) throws Exception {
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);
		
		controleur_INPORT = new FrigoInPort(dataOutPortURI, this);
		this.addPort(controleur_INPORT);
		controleur_INPORT.publishPort();
		
		this.tracer.setRelativePosition(0, 1);
		
		etat = EtatAppareil.ON;
		fridge_temperature = 4.4;
		fridge_temperature_cible = 3.0;
		freezer_temperature = 0.0;
		freezer_temperature_cible = -10.0;
		consommation = 55.0;
		this.type = type;
	}
	
	protected void freezerStabilize() {
		if(freezer_temperature_cible - freezer_temperature > 0.5) {
			freezer_temperature += 0.2;
			consommation--;
		} else if (freezer_temperature_cible - freezer_temperature < -0.5) {
			freezer_temperature -= 0.2;
			consommation++;
		}
	}
	
	protected void fridgeStabilize() {
		if(fridge_temperature_cible - fridge_temperature > 0.5) {
			fridge_temperature += 0.2;
			consommation++;
		} else if (fridge_temperature_cible - fridge_temperature < -0.5) {
			fridge_temperature -= 0.2;
			consommation--;
		}
	}
	
	
	@Override
	public void setEtatAppareil(EtatAppareil etat) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getConsommation() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setTemperature_Refrigerateur(double temperature) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTemperature_Congelateur(double temperature) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setLumiere_Refrigerateur(ModeFrigo mf) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setLumiere_Congelateur(ModeFrigo mf) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	protected void tick() throws Exception {
		if(etat == EtatAppareil.ON) {
			freezerStabilize();
			fridgeStabilize();
		}
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
					while(etat == EtatAppareil.ON) {
						Thread.sleep(2000);
						tick();
						this.taskOwner.logMessage("fridge : " + (Math.round(fridge_temperature*100.0)/100.0) + " °C");
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
			this.controleur_INPORT.unpublishPort();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
}
