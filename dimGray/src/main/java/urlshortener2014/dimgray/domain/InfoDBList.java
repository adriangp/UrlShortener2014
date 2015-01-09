package urlshortener2014.dimgray.domain;

import java.util.List;

/**
 * Esta clase contiene una lista de infoDBs 
 * @author Paulo
 *
 */

public class InfoDBList {

    private List<InfoDB> infoDBList;

    /**
     * Constructor de la clase vacío.
     */
    public InfoDBList() {
    }
    
    /**
     * Devuelve la lista almacenada en la clase.
     * @return la lista de URLs.
     */
    public List<InfoDB> getInfoDBList() {
        return infoDBList;
    }

    /**
     * Asigna una lista de UrlPair a un objeto de esta clase.
     * @param UrlPairs lista a asignar.
     */
    public void setInfoDBList(List<InfoDB> infoDBList) {
        this.infoDBList = infoDBList;
    }
}