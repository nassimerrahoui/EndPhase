package app;

import app.components.Assembleur;
import app.util.URI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractDistributedCVM;

public class DistributedCVM extends AbstractDistributedCVM {

	protected Assembleur assembleur;

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
					URI.JVM_ORDINATEUR_URI.getURI(),
					URI.JVM_PANNEAUSOLAIRE_URI.getURI(),
					URI.JVM_BATTERIE_URI.getURI(),
					URI.JVM_COMPTEUR_URI.getURI()
			};
			this.assembleur = new Assembleur(URI.DYNAMIC_ASSEMBLEUR_URI.getURI(), 1, 1, jvm_uris);
			this.assembleur.toggleTracing();
			this.assembleur.toggleLogging();
		}
		super.instantiateAndPublish();
	}

	@Override
	public void start() throws Exception {
		super.start();
		if (thisJVMURI.equals(URI.JVM_DYNAMIC_ASSEMBLEUR_URI.getURI())) {
			this.assembleur.runTask(new AbstractComponent.AbstractTask() {
				@Override
				public void run() {
					try {
						((Assembleur) this.getTaskOwner()).dynamicDeploy();
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			});
		}

	}

	public static void main(String[] args) {
		try {
			DistributedCVM dcvm = new DistributedCVM(args, 2, 5);
			dcvm.startStandardLifeCycle(100000L);
			Thread.sleep(5000L);
			System.exit(0);
		} catch (Exception e) { throw new RuntimeException(e); }
	}
}