package app.util;

/**
 * Si un compartiment est ouvert, alors la lumiere de ce compartiment est allumee
 * Sinon la lumiere est eteinte.
 * @author Willy Nassim
 *
 */
public enum ModeFrigo {
	
	OFF(0),
	LIGHT_OFF(1),
	LIGHT_ON(2);
	
	protected final int mode;
	 
    ModeFrigo(int m) {
        this.mode = m;
    }
 
    public int getMode() {
        return mode;
    }
}
