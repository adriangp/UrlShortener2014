package urlshortener2014.goldenPoppy.massiveLoad;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import urlshortener2014.common.domain.ShortURL;
import urlshortener2014.goldenPoppy.isAlive.URL;
import urlshortener2014.goldenPoppy.web.UrlShortenerControllerWithLogs;

public class Load implements Callable<List<Content>>{

	private static final Logger logger = LoggerFactory.getLogger(UrlShortenerControllerWithLogs.class);
	
	private List<Content> longUrls;
	private List<Content> shortUrls;
	private List<Content> timeouts;
	private UrlShortenerControllerWithLogs controller;
	private HttpServletRequest request;
	private int nShortened;
	
	/**
	 * Constructor
	 * 
	 * @param longs List with the long URLs to short.
	 * @param u Application controller.
	 * @param request Http request.
	 */
	public Load(List<Content> longs, 
			UrlShortenerControllerWithLogs u, HttpServletRequest request){
		this.longUrls = longs;
		this.shortUrls = new ArrayList<Content>();
		this.controller = u;
		this.request = request;
		this.timeouts = new ArrayList<Content>();
		this.nShortened = 0;
	}
	
	/**
	 * Main method to load some URLs. It's called by threads.
	 */
	@Override
	public List<Content> call() {
		
		for (Content c : longUrls){
			// First try to short the URLs.
			isAlive(c);
		}
		
		int tries = 0;
		while(nShortened < longUrls.size() && tries < 10){
			// If some URLs couldn't be shortened because the timeout has expired,
			// now they can be shortened in 10 new tries each one.
			for (Content c : timeouts){
				isAlive(c);
			}
			tries++;
		}
		
		if (tries == 10){
			// If the limit of tries has been reached, mark the URLs as invalids
			// and store them in the list of short URLs.
			for (Content c : timeouts){
				shortUrls.add(new Content(c.getId(), c.getURL(), 
						"The URL is not alive, so it can't be shortened!"));
			}
		}
		return shortUrls;
	}
	
	/**
	 * Method that allow test if an URL is alive and short it. If the URL
	 * is not alive, mark it as invalid. If the URL don't response in 2 seconds,
	 * put it in a list to test in the future if the URL is alive.
	 * 
	 * @param c Object that contains the URL and the sponsor to be shortened.
	 */
	public void isAlive(Content c) {
		try {
			switch(controller.isalive(new URL(c.getURL(),2)).getStatus()){
			case 0:
				// The timeout is expired. Add to the list of pending URLs.
				timeouts.add(c);
				logger.info("Massive load: Timeout expired to the URL: " + c.getURL());
				break;
			case -1:
				// The URL is not alive. Store the URL in the list of short URLs
				// marked as invalid.
				shortUrls.add(new Content(c.getId(), c.getURL(), "The URL is not alive, "
						+ "so it can't be shortened!"));
				if (timeouts.contains(c))
					timeouts.remove(c);
				nShortened++;
				logger.info("Massive load: The URL " + c.getURL() + " can't be shortened");
				break;
			default:
				// The URL is alive. Short and store in the list of short URLs.
				ResponseEntity<ShortURL> resp = controller.shortener(c.getURL(), 
						c.getSponsor(), null, request);
				
				//TODO: Aqui se pierde el control, el siguiente codigo no se ejecuta
				
				if (resp.getStatusCode() == HttpStatus.CREATED){
					shortUrls.add(new Content(c.getId(), c.getURL(), 
							resp.getHeaders().getLocation().toString()));
					logger.info("Massive load: The URL " + c.getURL() + " has been shortened");

				} else{
					shortUrls.add(new Content(c.getId(), c.getURL(), "The URL can't"
							+ "be shortened."));
				}
				if (timeouts.contains(c))
					timeouts.remove(c);
				nShortened++;
				break;
			}
		} catch (Exception e) {
			
		}
	}
}