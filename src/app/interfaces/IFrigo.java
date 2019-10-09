package app.interfaces;


public interface IFrigo extends IAppareil {
	
	public void setFreezerTemperatureCible(Double t) throws Exception;
	public void setFridgeTemperatureCible(Double t) throws Exception;
	
}
