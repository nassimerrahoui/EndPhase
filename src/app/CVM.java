package app;

import app.components.Assembleur;
import app.util.URI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;

public class CVM extends AbstractCVM {

	public CVM() throws Exception { super(); }
	
	@Override
	public void deploy() throws Exception {
		String[] jvm_uris = {
				AbstractCVM.thisJVMURI,
				AbstractCVM.thisJVMURI,
				AbstractCVM.thisJVMURI,
				AbstractCVM.thisJVMURI,
				AbstractCVM.thisJVMURI,
				AbstractCVM.thisJVMURI,
				AbstractCVM.thisJVMURI
		};
		
		@SuppressWarnings("unused")
		String assembleur = AbstractComponent.createComponent(
							Assembleur.class.getCanonicalName(),
							new Object[]{
									URI.DYNAMIC_ASSEMBLEUR_URI.getURI(),
									jvm_uris});
		super.deploy();
	}

	public static void main(String[] args) {
		try {
			CVM c = new CVM();
			c.startStandardLifeCycle(10000L);
			Thread.sleep(5000L);
			System.exit(0);
		} catch (Exception e) { throw new RuntimeException(e); }
	}
}