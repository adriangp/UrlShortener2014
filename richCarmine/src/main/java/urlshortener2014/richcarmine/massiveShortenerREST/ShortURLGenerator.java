package urlshortener2014.richcarmine.massiveShortenerREST;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import urlshortener2014.common.domain.ShortURL;
import urlshortener2014.richcarmine.massiveShortenerNaiveWS.CSVContent;
import urlshortener2014.richcarmine.web.UrlShortenerControllerWithLogs;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.Callable;

/**
 * Created by SAdrian on 05/01/2015.
 */
public class ShortURLGenerator extends RequestContextAwareCallable<CSVContent> {

    long order;
    String url;
    String sponsor;
    String brand;
    HttpServletRequest req;
    UrlShortenerControllerWithLogs controller;

    public ShortURLGenerator(long order, String url, String sponsor, String brand, HttpServletRequest req, UrlShortenerControllerWithLogs controller) {
        this.order = order;
        this.url = url;
        this.sponsor = sponsor;
        this.brand = brand;
        this.req = req;
        this.controller = controller;
    }

    @Override
    public CSVContent onCall() {
        ResponseEntity<ShortURL> response = controller.shortener(url,sponsor,brand,req);
        CSVContent content = new CSVContent();
        content.setOrder(order);
        content.setUrl(url);

        if(response.getStatusCode() == HttpStatus.BAD_REQUEST) content.setShortURL(null);
        else content.setShortURL(response.getBody().getUri().toString());

        return content;
    }
}
