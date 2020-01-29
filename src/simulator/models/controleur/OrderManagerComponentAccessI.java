package simulator.models.controleur;

import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI;

public interface OrderManagerComponentAccessI extends EmbeddingComponentAccessI{
	
	
	public void	controlTask(double simulatedTime) throws Exception ;
}
