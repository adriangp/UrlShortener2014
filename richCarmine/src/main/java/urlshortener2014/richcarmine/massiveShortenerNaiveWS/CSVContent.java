package urlshortener2014.richcarmine.massiveShortenerNaiveWS;

import urlshortener2014.common.domain.ShortURL;

/**
 * Created by SAdrian on 05/01/2015.
 */
public class CSVContent implements Comparable<CSVContent>{

    private long order;

    private String url;
    private String shortURL;

    public CSVContent() {
    }

    @Override
    public int compareTo(CSVContent o) {
        return (int) (order - o.order);
    }

    public String getShortURL() {
        return shortURL;
    }

    public long getOrder() {
        return order;
    }

    public String getUrl() {
        return url;
    }

    public void setOrder(long order) {
        this.order = order;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setShortURL(String shortURL) {
        this.shortURL = shortURL;
    }

    @Override
    public String toString() {
        return url + "," + shortURL;
    }
}
