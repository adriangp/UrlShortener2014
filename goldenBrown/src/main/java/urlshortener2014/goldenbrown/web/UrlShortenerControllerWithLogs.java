package urlshortener2014.goldenbrown.web;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
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
	private final static String BLACKLIST_ONREDIRECTTO_URI = 
	"http://localhost:8080/blacklist/onredirectto/?url={url}&date={date}&safe={safe}";
	private final static String BLACKLIST_ONSHORTENER_URI = 
			"http://localhost:8080/blacklist/onshortener/?url={url}";
	private final static String INTERSTITIAL_URI = 
			"http://localhost:8080/interstitial/?targetURL={targetURL}&interstitialURL={interstitialURL}";
	private final static String[] BANNER_URL = {
		"http://www.unizar.es",
		"http://www.rae.es/",
		"http://add.unizar.es/add/campusvirtual",
		"http://www.spamhaus.org/sbl/latest/",
		"https://es.wikipedia.org/wiki/Wikipedia:Portada"
	};
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
    		ResponseEntity<PlatformIdentity> respPlatform; 
    		ResponseEntity<?> respBlackList;
    		ResponseEntity<String> respInterstitial;
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
				
				if(l.getSponsor().length() != 0){
					respInterstitial = restTemplate.getForEntity(
							INTERSTITIAL_URI,
							String.class,
							l.getTarget(),
							l.getSponsor());
					return new ResponseEntity<>(respInterstitial.getBody(), 
							respInterstitial.getHeaders(), respInterstitial.getStatusCode());
				}
				else{
					return createSuccessfulRedirectToResponse(l);
				}
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
		boolean insertBanner = getSponsorParam(sponsor);
		if (insertBanner){
			sponsor = getRandomURL();
		}
		else{
			sponsor = "";
		}
		String id = Hashing.murmur3_32()
				.hashString(url+sponsor, StandardCharsets.UTF_8).toString();
		ShortURL shorturl;
		shorturl = shortURLRepository.findByKey(id);
		if (shorturl == null){
			shorturl = createAndSaveIfValid(id, url, sponsor, "", 
					UUID.randomUUID().toString(), extractIP(request));
		}
		else{
			// It seems to be a bug of the common framework
			// If a ShortURL is retrieved from the database, 
			// its "uri" field is null causing a NullPointerException
			// So we need to generate create ShortURL (because it does 
			// not allow to set a new URI
			shorturl = createShortURL(id, url, sponsor, UUID
					.randomUUID().toString(), extractIP(request));
		}
		
		if(insertBanner) { logger.info("Insert banner"); }
		
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<?> response = null;
		try{
			response = restTemplate.getForEntity(
						BLACKLIST_ONSHORTENER_URI,
						null,
						url);
			if (response.getStatusCode().equals(HttpStatus.OK)){
				HttpHeaders h = new HttpHeaders();
				h.setLocation(shorturl.getUri());
				return new ResponseEntity<>(shorturl, h, HttpStatus.CREATED);
			}
			else{
				return new ResponseEntity<ShortURL>(HttpStatus.UNPROCESSABLE_ENTITY);
			}
		}
		catch(HttpClientErrorException e){
			switch(e.getStatusCode()){
			case LOCKED:
				logger.info("Url "+url+" was considered spam.");
				return new ResponseEntity<ShortURL>(HttpStatus.LOCKED);
			default:
				return new ResponseEntity<ShortURL>(HttpStatus.UNPROCESSABLE_ENTITY);
			}
		}
		catch(Exception e){
			logger.info("Unkown Exception");
			return new ResponseEntity<ShortURL>(HttpStatus.BAD_REQUEST);
		}
	}
	
	private String getRandomURL() {
		Random r = new Random();
		return BANNER_URL[r.nextInt(BANNER_URL.length)];
	}

	private boolean getSponsorParam(String sponsor) {
		switch(sponsor){
		case "true":
			return true;
		case "false":
			return false;
		default:
			return false;
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
	
	protected ShortURL createAndSaveIfValid(String id, String url, String sponsor,
			String brand, String owner, String ip) {
		UrlValidator urlValidator = new UrlValidator(new String[] { "http",
				"https" });
		if (urlValidator.isValid(url)) {
			ShortURL su = createShortURL(id, url, sponsor, owner, ip);
			return shortURLRepository.save(su);
		} else {
			return null;
		}
	}

	private ShortURL createShortURL(String id, String url, String sponsor, String owner,
			String ip) {
		ShortURL su = new ShortURL(id, url,
				generateUri(id), sponsor, new Date(
						System.currentTimeMillis()), owner,
				HttpStatus.TEMPORARY_REDIRECT.value(), true, ip, null);
		return su;
	}

	private URI generateUri(String id) {
		return linkTo(
				methodOn(UrlShortenerControllerWithLogs.class).redirectTo(
						id, null)).toUri();
	}
}

