package urlshortener2014.dimgray.domain;

import java.util.List;

/**
 * Esta clase contiene una lista de urls junto a sus urls acortadas respectivas.
 * @author Ivan
 *
 */

public class UrlPairs {

    private List<UrlPair> UrlPairs;

    /**
     * Constructor de la clase vacío.
     */
    public UrlPairs() {
    }
    
    /**
     * Devuelve la lista almacenada en la clase.
     * @return la lista de URLs.
     */
    public List<UrlPair> getUrlPairs() {
        return UrlPairs;
    }

    /**
     * Asigna una lista de UrlPair a un objeto de esta clase.
     * @param UrlPairs lista a asignar.
     */
    public void setUrlPairs(List<UrlPair> UrlPairs) {
        this.UrlPairs = UrlPairs;
    }
}