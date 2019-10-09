package app.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;

public interface IAppareil extends OfferedI {
	
	public void setOn() throws Exception;
	public void setOff() throws Exception;
}
