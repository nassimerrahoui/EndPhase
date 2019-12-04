package app.util;

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
