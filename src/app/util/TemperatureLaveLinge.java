package app.util;

/**
 * La temperature est en degre celsuis °C
 * afin de definir la temperature lors du mode lavage et du rincage du lave-linge
 * @author Willy Nassim
 *
 */
public enum TemperatureLaveLinge {

	VINGT_DEGRES(500),
	TRENTE_DEGRES(600),
	QUARANTE_DEGRES(700),
	SOIXANTE_DEGRES(800),
	QUATRE_VINGT_DIX_DEGRES(900);
	
	protected final double consommation;
	 
    private TemperatureLaveLinge (double consommation) {
        this.consommation = consommation;
    }
 
    public double getConsommation() {
        return consommation;
    }
    
    
}
