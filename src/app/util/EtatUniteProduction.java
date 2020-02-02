package app.util;

/**
 * Liste les etats possibled d'une unite de production
 * ON = l'unite de production est allumee
 * OFF = l'unite de production est eteinte
 * @author Willy Nassim
 *
 */
public enum EtatUniteProduction {
	OFF(0),
	ON(1);
	
	protected final int valeur;

	private EtatUniteProduction(int valeur) {
		this.valeur = valeur;
	}
	
	public int getValeur() {
		return valeur;
	}
}
