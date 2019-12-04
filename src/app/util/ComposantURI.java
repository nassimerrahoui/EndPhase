package app.util;

public enum ComposantURI {
	
	CONTROLEUR_URI("controleurURI"),
	FRIGO_URI("frigoURI"),
	LAVELIGNE_URI("lavelingeURI"),
	ORDINATEUR_URI("ordiURI"),
	BATTERIE_URI("batterieURI"),
	PANNEAUSOLAIRE_URI("panneauURI"),
	COMPTEUR_URI("compteurURI");
	
	private String uri;
	 
    ComposantURI(String envUri) {
        this.uri = envUri;
    }
 
    public String getURI() {
        return uri;
    }
}
