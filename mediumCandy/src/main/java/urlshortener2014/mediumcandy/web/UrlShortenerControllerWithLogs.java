package urlshortener2014.mediumcandy.web;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.hash.Hashing;

import urlshortener2014.common.domain.ShortURL;
import urlshortener2014.common.web.UrlShortenerController;

@RestController
public class UrlShortenerControllerWithLogs extends UrlShortenerController {

	private static final Logger logger = LoggerFactory.getLogger(UrlShortenerControllerWithLogs.class);
	
	public ResponseEntity<?> redirectTo(@PathVariable String id, 
			HttpServletRequest request) {
		logger.info("Requested redirection with hash "+id);
		return super.redirectTo(id, request);
	}

	public ResponseEntity<ShortURL> shortener(@RequestParam("url") String url,
			@RequestParam(value = "sponsor", required = false) String sponsor,
			@RequestParam(value = "brand", required = false) String brand,
			HttpServletRequest request) {
		logger.info("Requested new short for uri "+url);
		return super.shortener(url, sponsor, brand, request);
	}
	
	/**
	 * Pings a HTTP URL. This effectively sends a HEAD request and returns TRUE if the response code is in 
	 * the 200-399 range.
	 * @param urlIn The HTTP URL to be pinged.
	 * @return TRUE if the given HTTP URL has returned response code 200-399 on a HEAD request within the
	 * given timeout, otherwise FALSE.
	 */
	private static boolean ping(String urlIn) {
		// Otherwise an exception may be thrown on invalid SSL certificates.
	    String url = urlIn.replaceFirst("https", "http");
	    
	    // The timeout in millis for both the connection timeout and the response read timeout. 
		// Note that the total timeout is effectively two times the given timeout.
	    int timeout = 2000;

	    try {
	    	
	        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
	        connection.setConnectTimeout(timeout);
	        connection.setReadTimeout(timeout);
	        connection.setRequestMethod("HEAD");
	        int responseCode = connection.getResponseCode();
	        
	        return (200 <= responseCode && responseCode <= 399);
	        
	    } catch (IOException exception) {
	        return false;
	    }
	}
	
	/**
	 * Shortens a given URL if this URL is reachable via HTTP.
	 */
	@RequestMapping(value = "/linkreachable", method = RequestMethod.POST)
	public ResponseEntity<ShortURL> shortenerIfReachable(@RequestParam("url") String url,
			@RequestParam(value = "sponsor", required = false) String sponsor,
			@RequestParam(value = "brand", required = false) String brand,
			HttpServletRequest request) {
		ShortURL su = null;
		boolean isReachableUrl = ping(url);
		
		if (isReachableUrl){
			su = createAndSaveIfValid(url, sponsor, brand, UUID
					.randomUUID().toString(), extractIP(request));
		}
		
		if (su != null) {
			HttpHeaders h = new HttpHeaders();
			h.setLocation(su.getUri());
			return new ResponseEntity<>(su, h, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
}

