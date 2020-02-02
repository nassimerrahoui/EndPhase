package app;

import app.components.Assembleur;
import app.util.URI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractDistributedCVM;

/**
 * @author Willy Nassim
 */

public class DistributedCVM extends AbstractDistributedCVM {

	public DistributedCVM(String[] args, int xLayout, int yLayout) throws Exception {
		super(args, xLayout, yLayout);
	}

	@Override
	public void initialise() throws Exception {
		super.initialise();
	}

	@Override
	public void instantiateAndPublish() throws Exception {
		if (thisJVMURI.equals(URI.JVM_DYNAMIC_ASSEMBLEUR_URI.getURI())) {
			
			String[] jvm_uris = {
					URI.JVM_CONTROLEUR_URI.getURI(),
					URI.JVM_FRIGO_URI.getURI(),
					URI.JVM_LAVELINGE_URI.getURI(),
					URI.JVM_ASPIRATEUR_URI.getURI(),
					URI.JVM_PANNEAUSOLAIRE_URI.getURI(),
					URI.JVM_BATTERIE_URI.getURI(),
					URI.JVM_COMPTEUR_URI.getURI()
			};
			
			@SuppressWarnings("unused")
			String assembleur = AbstractComponent.createComponent(
								Assembleur.class.getCanonicalName(),
								new Object[]{
										URI.DYNAMIC_ASSEMBLEUR_URI.getURI(),
										jvm_uris});
		
		}
		super.instantiateAndPublish();
	}

	public static void main(String[] args) {
		try {
			DistributedCVM dda = new DistributedCVM(args, 2, 5);
			dda.startStandardLifeCycle(40000L);
			Thread.sleep(5000L);
			System.exit(0);
		} catch (Exception e) { throw new RuntimeException(e); }
	}
}