package urlshortener2014.mediumcandy.web;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import urlshortener2014.common.domain.ShortURL;
import urlshortener2014.common.web.UrlShortenerController;
import urlshortener2014.mediumcandy.domain.ClickStats;

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
	public ShortURL shortenerIfReachable(@RequestParam("url") String url,
			@RequestParam(value = "sponsor", required = false) String sponsor,
			@RequestParam(value = "brand", required = false) String brand,
			HttpServletRequest request) {
		ShortURL su = null;
		boolean isReachableUrl = ping(url);
		
		if (isReachableUrl){
			su = createAndSaveIfValid(url, sponsor, brand, UUID
					.randomUUID().toString(), extractIP(request));
		}
		
		return su;
	}
	
	@RequestMapping(value = "/linkstats", method = RequestMethod.GET)
	public ResponseEntity<List<ClickStats>> shortenerIfReachable(@RequestParam("url") String url){
		
		List<ShortURL> listShortURL;
		Long infoClicks; 
		List<ClickStats> listResult = new ArrayList<ClickStats>();
		listShortURL = shortURLRepository.findByTarget(url);
		if(listShortURL.size()==0){
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			
		}else{
			for(ShortURL su: listShortURL){
				infoClicks = clickRepository.clicksByHash(su.getHash());
				ClickStats cs = new ClickStats(url, infoClicks, su.getOwner(), su.getUri());
				listResult.add(cs); 
			}
			return new ResponseEntity<>(listResult, HttpStatus.OK);
		}
	}
}

