package app.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;

public interface IFrigo extends IAppareil, OfferedI {
	
	public void setFreezerTemperatureCible(Double t);
	public void setFridgeTemperatureCible(Double t);
	
}
