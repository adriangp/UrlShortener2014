package urlshortener2014.goldenbrown.web;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.google.common.hash.Hashing;

import urlshortener2014.common.domain.Click;
import urlshortener2014.common.domain.ShortURL;
import urlshortener2014.common.web.UrlShortenerController;
import urlshortener2014.goldenbrown.platformidentifier.PlatformIdentity;
/**
 * Main class project. This class control the URLshortener creation by the method shorneted
 * and the redirection management with the user click
 * @author: Jorge,Javi,Gabi
 * @version: 08/01/2015
 */
@RestController
public class UrlShortenerControllerWithLogs extends UrlShortenerController {
	
	private static final Logger logger = LoggerFactory.getLogger(UrlShortenerControllerWithLogs.class);
	private final static String PLATFORMIDENTIFIER_URI = 
			"http://localhost:8080/platformidentifier/?us={us}";
//	private final static String BLACKLIST_ONREDIRECTTO_URI = 
//			"http://localhost:8080/blacklist/onredirectto/?url={url}?date={date}?safe={safe}";
	private final static String BLACKLIST_ONREDIRECTTO_URI = 
	"http://localhost:8080/blacklist/onredirectto/?url={url}&date={date}&safe={safe}";
	private final static String BLACKLIST_ONSHORTENER_URI = 
			"http://localhost:8080/blacklist/onshortener/?url={url}";

	/**
	 * Method that managent all refer to user click about the short URL
	 * In this method realize the identification of the platform and navigator using the User-Agent
	 * del User-Agent
	 * @param id id of the short URL
	 * @param request object used for know the user information using his User-Agent
	 */
	@Override
	@RequestMapping(value = "/l{id}", method = RequestMethod.GET)
	public ResponseEntity<?> redirectTo(@PathVariable String id,
			HttpServletRequest request) {
		ShortURL l = shortURLRepository.findByKey(id);
        if (l != null) {
        	String useragentstring = "", browser = "", platform = "";	
    		useragentstring = request.getHeader("User-Agent");
    		
    		RestTemplate restTemplate = new RestTemplate();
    		ResponseEntity<PlatformIdentity> respPlatform, respBlackList;
    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    		String dateString = sdf.format(l.getCreated());
    		try{
    			respBlackList = restTemplate.getForEntity(
    					BLACKLIST_ONREDIRECTTO_URI,
						null,
						l.getTarget(),
						dateString,
						l.getSafe());
    			
    			// If link has been classified as not-spam recently
				if (respBlackList.getStatusCode().equals(HttpStatus.OK)){ 
				}
				// If link has been just classified as not-spam right now => Update its creation date
				else if (respBlackList.getStatusCode().equals(HttpStatus.CREATED)){
					// Update "created" field of the link
					l = updateShortUrl(l, true);
				}
				else{
					createAndSaveClick(id, extractIP(request));
					return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
				}
				// If BlackList validation fails (link is spam), then either "else" block is
				// executed or an exception is thrown. Therefore, if blacklist validation
				// fails, no link is returned. Otherwise, continue with platform identifier
				
				respPlatform = restTemplate.getForEntity(
	    						PLATFORMIDENTIFIER_URI,
	    						PlatformIdentity.class,
	    						useragentstring);
	    		if (respPlatform.getStatusCode().equals(HttpStatus.OK)){
	    			PlatformIdentity pi = respPlatform.getBody();
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
    		}
    		catch(HttpClientErrorException e){
    			switch(e.getStatusCode()){
    			case BAD_REQUEST:
    				logger.info("BAD_REQUEST");
    				createAndSaveClick(id, extractIP(request));
    				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    			case LOCKED:
    				logger.info("Url "+l.getTarget()+" was considered spam.");
					// Update "created" field of the link
					l = updateShortUrl(l, false);
					createAndSaveClick(id, extractIP(request));
    				return new ResponseEntity<>(HttpStatus.LOCKED);
    			default:
    				logger.info("Unkown HttpClientErrorException");
    				createAndSaveClick(id, extractIP(request));
    				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    			}
    		}
    		catch(Exception e){
    			logger.info("Unkown Exception");
				createAndSaveClick(id, extractIP(request));
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    		}
		}  
        else { // If l == null
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Method that short a user URL, it call the shortener method of the commonn
	 * @param url URL that user wants shortener
	 */
	@Override
	public ResponseEntity<ShortURL> shortener(@RequestParam("url") String url,
			@RequestParam(value = "sponsor", required = false) String sponsor,
			@RequestParam(value = "brand", required = false) String brand,
			HttpServletRequest request) {
		logger.info("Requested new short for uri "+url);
		ShortURL shorturl = new ShortURL();
		
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<?> response = null;
		try{
			response = restTemplate.getForEntity(
						BLACKLIST_ONSHORTENER_URI,
						null,
						url);
			if (response.getStatusCode().equals(HttpStatus.OK)){
				return super.shortener(url, sponsor, brand, request);
			}
			else{
				return new ResponseEntity<ShortURL>(shorturl, HttpStatus.BAD_REQUEST);
			}
		}
		catch(HttpClientErrorException e){
			switch(e.getStatusCode()){
			case LOCKED:
				logger.info("Url "+url+" was considered spam.");
				return new ResponseEntity<ShortURL>(shorturl, HttpStatus.LOCKED);
			default:
				return new ResponseEntity<ShortURL>(shorturl, HttpStatus.BAD_REQUEST);
			}
		}
		catch(Exception e){
			logger.info("Unkown Exception");
			return new ResponseEntity<ShortURL>(shorturl, HttpStatus.BAD_REQUEST);
		}
	}
	
	/**
	 * This method create a Click object with the collection information in the method redirectTo
	 * for send to the common clickRepository and save the click information with the information 
	 * of the navigator and platform that press in the click, plus the IP, date and id of the URL.
	 * @param hash of the short URL that was pressed
	 * @param ip user ip
	 * @param browser user browser
	 * @param platform platform user
	 */
	protected void createAndSaveClick(String hash, String ip, String browser, String platform) {
		Click cl = new Click(null, hash, new Date(System.currentTimeMillis()),
				null, browser, platform, ip, null);
		clickRepository.save(cl);
	}
	/**
	 * This method  update the date of a given short URL.
	 * @param oldsu short URL to update
	 * @param safe parameter for know is safe or not (is in blacklist)
	 */
	protected ShortURL updateShortUrl(ShortURL oldsu, boolean safe) {
		Date now = new Date(System.currentTimeMillis());
		ShortURL su = new ShortURL(oldsu.getHash(), oldsu.getTarget(), oldsu.getUri(),
				oldsu.getSponsor(), now, oldsu.getOwner(), oldsu.getMode(),
				safe, oldsu.getIP(), oldsu.getCountry());
		shortURLRepository.update(su);
		return su;
	}
}

