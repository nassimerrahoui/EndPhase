package app.util;

/**
 * 
 * @author Nassim Willy
 *
 */
public enum URI {
	
	/** URI de l'assembleur */
	DYNAMIC_ASSEMBLEUR_URI("DYNAMIC_ASSEMBLEUR_URI"),
	
	/** URI des composants */
	CONTROLEUR_URI("CONTROLEUR_URI"),
	FRIGO_URI("FRIGO_URI"),
	LAVELINGE_URI("LAVELINGE_URI"),
	ORDINATEUR_URI("ORDINATEUR_URI"),
	BATTERIE_URI("BATTERIE_URI"),
	PANNEAUSOLAIRE_URI("PANNEAUSOLAIRE_URI"),
	COMPTEUR_URI("COMPTEUR_URI"),
	
	/** URI des ports sortants controleur */
	CONTROLEUR_OP_FRIGO_URI("CONTROLEUR_OP_FRIGO_URI"),
	CONTROLEUR_OP_ORDINATEUR_URI("CONTROLEUR_OP_ORDINATEUR_URI"),
	CONTROLEUR_OP_LAVELINGE_URI("CONTROLEUR_OP_LAVELINGE_URI"),
	CONTROLEUR_OP_COMPTEUR_URI("CONTROLEUR_OP_COMPTEUR_URI"),
	CONTROLEUR_OP_PANNEAUSOLAIRE_URI("CONTROLEUR_OP_PANNEAUSOLAIRE_URI"),
	CONTROLEUR_OP_BATTERIE_URI("CONTROLEUR_OP_BATTERIE_URI"),
	
	/** URI des ports sortants appareils et unites de productions vers controleur */
	FRIGO_CONTROLEUR_OP_URI("FRIGO_CONTROLEUR_OP_URI"),
	LAVELINGE_CONTROLEUR_OP_URI("LAVELINGE_CONTROLEUR_OP_URI"),
	ORDINATEUR_CONTROLEUR_OP_URI("ORDINATEUR_CONTROLEUR_OP_URI"),
	PANNEAUSOLAIRE_CONTROLEUR_OP_URI("PANNEAUSOLAIRE_CONTROLEUR_OP_URI"),
	BATTERIE_CONTROLEUR_OP_URI("BATTERIE_CONTROLEUR_OP_URI"),
	
	/** URI des ports sortants appareils et unites de productions vers compteur */
	FRIGO_COMPTEUR_OP_URI("FRIGO_COMPTEUR_OP_URI"),
	LAVELINGE_COMPTEUR_OP_URI("LAVELINGE_COMPTEUR_OP_URI"),
	ORDINATEUR_COMPTEUR_OP_URI("ORDINATEUR_COMPTEUR_OP_URI"),
	PANNEAUSOLAIRE_COMPTEUR_OP_URI("PANNEAUSOLAIRE_COMPTEUR_OP_URI"),
	BATTERIE_COMPTEUR_OP_URI("BATTERIE_COMPTEUR_OP_URI"),
	
	/** URI JVM de l'assembleur */
	JVM_DYNAMIC_ASSEMBLEUR_URI("JVM_DYNAMIC_ASSEMBLEUR_URI"),
	
	/** URI JVM des composants */
	JVM_CONTROLEUR_URI("JVM_CONTROLEUR_URI"),
	JVM_FRIGO_URI("JVM_FRIGO_URI"),
	JVM_LAVELINGE_URI("JVM_LAVELINGE_URI"),
	JVM_ORDINATEUR_URI("JVM_ORDINATEUR_URI"),
	JVM_BATTERIE_URI("JVM_BATTERIE_URI"),
	JVM_PANNEAUSOLAIRE_URI("JVM_PANNEAUSOLAIRE_URI"),
	JVM_COMPTEUR_URI("JVM_COMPTEUR_URI"),
	
	/** URI pool de threads composants */
	POOL_AJOUT_CONTROLEUR_URI("POOL_AJOUT_CONTROLEUR_URI"),
	POOL_CONSO_PROD_CONTROLEUR_URI("POOL_CONSO_PROD_CONTROLEUR_URI"),
	POOL_ACTION_FRIGO_URI("POOL_ACTION_FRIGO_URI"),
	POOL_ACTION_LAVELINGE_URI("POOL_ACTION_LAVELINGE_URI"),
	POOL_ACTION_ORDINATEUR_URI("POOL_ACTION_ORDINATEUR_URI"),
	POOL_ACTION_PANNEAUSOLAIRE_URI("POOL_ACTION_PANNEAUSOLAIRE_URI"),
	POOL_ACTION_BATTERIE_URI("POOL_ACTION_BATTERIE_URI"),
	POOL_CONTROLE_COMPTEUR_URI("POOL_CONTROLE_COMPTEUR_URI"),
	POOL_CONSO_PROD_COMPTEUR_URI("POOL_CONSO_PROD_COMPTEUR_URI");
	
	protected final String uri;
	 
    URI(String envUri) {
        this.uri = envUri;
    }
 
    public String getURI() {
        return uri;
    }
}
