package urlshortener2014.goldenPoppy.massiveLoad;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import urlshortener2014.common.domain.ShortURL;
import urlshortener2014.goldenPoppy.isAlive.URL;
import urlshortener2014.goldenPoppy.web.UrlShortenerControllerWithLogs;

public class Load extends RequestContextAwareCallable<Content>{

	private static final Logger logger = LoggerFactory.getLogger(UrlShortenerControllerWithLogs.class);
	
	private Content longUrl;
	private Content shortUrl;
	private UrlShortenerControllerWithLogs controller;
	private HttpServletRequest request;

	/**
	 * Constructor
	 * 
	 * @param longUrl Long URL to short.
	 * @param request Http request.
	 */
	public Load(Content longUrl, UrlShortenerControllerWithLogs u, HttpServletRequest request){
		this.longUrl = longUrl;
		this.request = request;
		this.controller = u;
	}
	
	/**
	 * Main method to load a URL. It's called by threads.
	 * 
	 * @return Object with the URL shortened.
	 */
	@Override
	public Content onCall() throws Exception {
		shortUrl =  isAlive(longUrl);
		
		int tries = 0;
		while(shortUrl.getSponsor().equals(longUrl.getSponsor()) && tries < 10){
			// If an URL couldn't be shortened because the timeout has expired,
			// now it can be shortened in 10 new tries.
			shortUrl =  isAlive(longUrl);;
		}
		
		if (tries == 10){
			// If the limit of tries has been reached, mark the URL as invalid
			// and return it.
			return (new Content(longUrl.getId(), longUrl.getURL(), 
					"The URL is not alive, so it can't be shortened!"));
		}
		return shortUrl;
	}
	
	/**
	 * Method that allow test if an URL is alive and short it. If the URL
	 * is not alive, mark it as invalid. If the URL don't response in 2 seconds,
	 * return the same long URL to try to short later.
	 * 
	 * @param c Object that contains the URL and the sponsor to be shortened.
	 * @return Object with the result of the short.
	 */
	private Content isAlive(Content c) {
		
		
		
		switch(controller.isalive(new URL(c.getURL(),2)).getStatus()){
		case 0:
			// The timeout is expired.
			logger.info("Massive load: Timeout expired to the URL: " + c.getURL());
			return c;
		case -1:
			// The URL is not alive. Store the URL in the list of short URLs
			// marked as invalid.
			logger.info("Massive load: The URL " + c.getURL() + " can't be shortened");
			return (new Content(c.getId(), c.getURL(), "The URL is not alive, "
					+ "so it can't be shortened!"));
		default:
			// The URL is alive. Short and store in the list of short URLs.
			String resp =  linkTo(methodOn(UrlShortenerControllerWithLogs.class).
					shortener(c.getURL(), c.getSponsor(), null, request)).toString();
			
			RestTemplate rt = new RestTemplate();
			ShortURL sUrl = rt.postForObject(resp, null, ShortURL.class);
			
			try{
				if (sUrl.getUri() != null){
					logger.info("Massive load: The URL " + c.getURL() + " has been shortened");
					return (new Content(c.getId(), c.getURL(),sUrl.getUri().toString()));
				}
			} catch (Exception e){
				return (new Content(c.getId(), c.getURL(), "The URL can't"
						+ "be shortened."));
			}
		}
		return (new Content(c.getId(), c.getURL(), "The URL can't"
				+ "be shortened."));
	}
}