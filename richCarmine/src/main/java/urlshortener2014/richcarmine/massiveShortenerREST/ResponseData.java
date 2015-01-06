package urlshortener2014.richcarmine.massiveShortenerREST;

import urlshortener2014.richcarmine.massiveShortenerNaiveWS.CSVContent;

import java.util.List;

/**
 * Created by SAdrian on 06/01/2015.
 */
public class ResponseData {
    private String uri;
    private List<CSVContent> csv;

    public ResponseData() {
    }

    public String getUri() {
        return uri;
    }

    public List<CSVContent> getCsv() {
        return csv;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setCsv(List<CSVContent> csv) {
        this.csv = csv;
    }
}
