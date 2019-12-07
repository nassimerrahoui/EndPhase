package app.components;


import app.interfaces.production.IPanneau;
import app.interfaces.production.IProduction;
import app.util.EtatUniteProduction;
import fr.sorbonne_u.components.AbstractComponent;


public class PanneauSolaire extends AbstractComponent implements IPanneau, IProduction {

	protected boolean isOn;
	protected Double production;
	
	public PanneauSolaire(String reflectionInboundPortURI, int nbThreads, int nbSchedulableThreads, String dataOutPortURI) throws Exception {
		
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);

		isOn = false;
		production = 0.0;
		
		this.tracer.setRelativePosition(1, 2);
	}
	
	
	@Override
	public void setEtatUniteProduction(EtatUniteProduction etat) throws Exception {
		// TODO Auto-generated method stub
		
	}


	@Override
	public double getProduction() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}
	

}
