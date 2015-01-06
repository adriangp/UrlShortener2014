package urlshortener2014.richcarmine.massiveShortenerNaiveWS;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import urlshortener2014.common.domain.ShortURL;
import urlshortener2014.richcarmine.massiveShortenerREST.RequestContextAwareCallable;
import urlshortener2014.richcarmine.web.UrlShortenerControllerWithLogs;

import java.net.InetSocketAddress;

/**
 * Created by SAdrian on 06/01/2015.
 */
public class ShortURLWSGenerator extends RequestContextAwareCallable<CSVContent>{

    private static final Logger logger = LoggerFactory.getLogger(ShortURLWSGenerator.class);
    long order;
    String url;
    String sponsor;
    String brand;
    String owner;
    InetSocketAddress address;
    UrlShortenerControllerWithLogs controller;

    public ShortURLWSGenerator(long order, String url, String sponsor, String brand, String owner, InetSocketAddress address, UrlShortenerControllerWithLogs controller) {
        this.order = order;
        this.url = url;
        this.sponsor = sponsor;
        this.brand = brand;
        this.owner = owner;
        this.address = address;
        this.controller = controller;
    }

    @Override
    public CSVContent onCall() {

        ShortURL shortURL = null;
        try {
            shortURL = controller. new CreateCallable(url,sponsor,brand,owner,address.toString()).call();
        } catch (Exception e) {
            e.printStackTrace();
        }

        CSVContent content = new CSVContent();
        content.setOrder(order);
        content.setShortURL(shortURL);

        return content;
    }
}
