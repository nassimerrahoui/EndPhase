package app.util;

/**
 * La temperature est en degre celsuis °C
 * afin de definir la temperateur lors du mode lavage et du rincage du lave-linge
 * @author Willy Nassim
 *
 */
public enum TemperatureLaveLinge {

	VINGT_DEGRES(55),
	TRENTE_DEGRES(60),
	QUARANTE_DEGRES(70),
	SOIXANTE_DEGRES(80),
	QUATRE_VINGT_DIX_DEGRES(85);
	
	protected final double consommation;
	 
    private TemperatureLaveLinge (double consommation) {
        this.consommation = consommation;
    }
 
    public double getConsommation() {
        return consommation;
    }
    
    
}
