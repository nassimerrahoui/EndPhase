package simulator.models.controleur;

import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI;

/**
 * @author Willy Nassim
 */

/** interface de controle utilise par le composant controleur */
public interface OrderManagerComponentAccessI extends EmbeddingComponentAccessI{

	public void	controlTask(double simulatedTime) throws Exception ;
}
