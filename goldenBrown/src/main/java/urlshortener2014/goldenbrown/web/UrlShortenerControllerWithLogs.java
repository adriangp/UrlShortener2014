package urlshortener2014.goldenbrown.web;

import java.sql.Date;

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
import org.springframework.web.client.RestTemplate;

import urlshortener2014.common.domain.Click;
import urlshortener2014.common.domain.ShortURL;
import urlshortener2014.common.web.UrlShortenerController;
import urlshortener2014.goldenbrown.platformidentifier.PlatformIdentity;

@RestController
public class UrlShortenerControllerWithLogs extends UrlShortenerController {
	
	private static final Logger logger = LoggerFactory.getLogger(UrlShortenerControllerWithLogs.class);
	private final static String PLATFORMIDENTIFIER_URI = 
			"http://localhost:8080/platformidentifier/?us={us}";
	private final static String BLACKLIST_ONREDIRECTTO_URI = 
			"http://localhost:8080/blacklist/onredirectto/?url={url}";
	private final static String BLACKLIST_ONSHORTENER_URI = 
			"http://localhost:8080/blacklist/onshortener/?url={url}";
	@Override
	@RequestMapping(value = "/l{id}", method = RequestMethod.GET)
	public ResponseEntity<?> redirectTo(@PathVariable String id,
			HttpServletRequest request) {
		ShortURL l = shortURLRepository.findByKey(id);
        if (l != null) {
        	String useragentstring = "", browser = "", platform = "";	
    		useragentstring = request.getHeader("User-Agent");
    		
    		RestTemplate restTemplate = new RestTemplate();
    		ResponseEntity<PlatformIdentity> response = restTemplate.getForEntity(
    						PLATFORMIDENTIFIER_URI,
    						PlatformIdentity.class,
    						useragentstring);
    		
    		if (response.getStatusCode().equals(HttpStatus.OK)){
    			PlatformIdentity pi = response.getBody();
    			if(pi.getVersion().equals("UNKNOWN")){
    				browser = pi.getBrowser();
    			}
    			else{
    				browser = pi.getBrowser() + " " + pi.getVersion();
    			}
    			platform = pi.getOs();
    		}
			createAndSaveClick(id, extractIP(request), browser, platform);
			return createSuccessfulRedirectToResponse(l);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@Override
	public ResponseEntity<ShortURL> shortener(@RequestParam("url") String url,
			@RequestParam(value = "sponsor", required = false) String sponsor,
			@RequestParam(value = "brand", required = false) String brand,
			HttpServletRequest request) {
		logger.info("Requested new short for uri "+url);
		ShortURL shorturl = new ShortURL();
		
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<?> response = restTemplate.getForEntity(
				BLACKLIST_ONSHORTENER_URI,
				PlatformIdentity.class,
				url);
		if (response.getStatusCode().equals(HttpStatus.OK)){
			return super.shortener(url, sponsor, brand, request);
		}
		else if (response.getStatusCode().equals(HttpStatus.LOCKED)){
			logger.info("Url "+url+" was considered spam.");
			return new ResponseEntity<ShortURL>(shorturl, HttpStatus.LOCKED);
		}
		else{
			return new ResponseEntity<ShortURL>(shorturl, HttpStatus.BAD_REQUEST);
		}
	}
	
	protected void createAndSaveClick(String hash, String ip, String browser, String platform) {
		Click cl = new Click(null, hash, new Date(System.currentTimeMillis()),
				null, browser, platform, ip, null);
		clickRepository.save(cl);
	}
}

