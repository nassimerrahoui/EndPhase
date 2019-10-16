package app.data;

import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataRequiredI;

public class Message implements DataOfferedI.DataI, DataRequiredI.DataI {

	private static final long serialVersionUID = 1L;
	protected String dateEmission;
	protected String contenu;
	
	public String getContenu() {
		return contenu;
	}
	
	public void setContenu(String contenu) {
		this.contenu = contenu;
	}
}
