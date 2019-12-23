package app.util;

/**
 * Type d'appareil servant a definir un ordre de priorite de consommation d'energie 
 * La gestion de l'ordre de priorite est effectuee par le controleur
 * @author Willy Nassim
 *
 */
public enum TypeAppareil {
	
	CONSO_PERMANENTE(1),
	CONSO_PLANIFIABLE(2),
	CONSO_INCONTROLABLE(3);
	
	private final int value;
	
    private TypeAppareil(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
