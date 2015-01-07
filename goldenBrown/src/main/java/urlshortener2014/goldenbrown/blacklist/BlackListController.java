package urlshortener2014.goldenbrown.blacklist;

import java.net.MalformedURLException;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

/**
 * Class that provides a web service  that use third parties BD for prove the IP of the 
 * URL to shortener. This option is rebuilt from a PHP program. Also, we use a cache for 
 * avoid bomb the service if the IP's are know.
 * @author: Jorge,Javi,Gabi
 * @version: 08/01/2015
 */
@RestController
@RequestMapping("/blacklist")
public class BlackListController {
	
	@Autowired
	BlackListService blackListService;
	
	private static final Logger logger = LoggerFactory.getLogger(BlackListController.class);
	
	/**
	 * This method receive an URL and call a private method to prove is the URL is in the blacklsit
	 * or not
	 * @param urlString: url introduced manually by the user
	 * 			It represents an accessible URL
	 * @return OK if the url isn't in the blacklist and LOCKED if it is
	 */
	@RequestMapping(value = "/onshortener", method = RequestMethod.GET)
	public ResponseEntity<?> onShortener(@RequestParam("url") String urlString){
		return askBlackListService(urlString);
	}

	
	
	/**
	 * This method ask again to the third databases if that url is in the blacklist.
	 * A url can be safe and become unsafe, so this method proves that that url dont become unsafe.
	 * @param urlString: url pointed by some of our links
	 * @return OK if the url isn't in the blacklist and LOCKED if it is nad BAD REQUEST if there is any error
	 */
	@RequestMapping(value = "/onredirectto", method = RequestMethod.GET)
	public ResponseEntity<?> onRedirectTo(@RequestParam("url") String urlString,
			@RequestParam("date") String dateString, @RequestParam("safe") boolean safe) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try{
			date = sdf.parse(dateString);
		}
		catch (java.text.ParseException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		if(oldLink(date)){
			// Then ask again to the blacklist service
			return askBlackListService(urlString);
		}
		else{ // No need to ask to the blacklist service
			if(safe){
				return new ResponseEntity<>(HttpStatus.OK);
			}
			else{
				return new ResponseEntity<>(HttpStatus.LOCKED);
			}
		}
		
	}
	/**
	 * This method invoke the blackListService if the url is in the blacklist
	 * @param urlString URL that we prove if it is in the blacklist or not
	 * @return OK if the url isn't in the blacklist and LOCKED if it is
	 */
	private ResponseEntity<?> askBlackListService(String urlString) {
		URL url = null;
		String host= "";
		boolean blacklisted = false;
		try{
			// Sanitize URL (If it's not valid, it will throw a MalformedURLException)
			url = new URL(urlString);
			host = url.getHost();
			//Remove www. from domain (but not from www.com)
			try{
				host = host.replace("www.", "");
			}
			catch(NullPointerException e){ }
			catch(Exception e){ }
		    blacklisted = blackListService.isBlackListed(host);
		    if (!blacklisted){
		    	logger.info("\""+urlString + "\" is Not Blacklisted.");
		    	return new ResponseEntity<>(HttpStatus.OK);
		    }
		    else{
		    	logger.info("\""+urlString + "\" is Blacklisted.");
		    	return new ResponseEntity<>(HttpStatus.LOCKED);
		    }
		}
		catch(IllegalArgumentException e){
			logger.error("Bad Request, cannot check if \""+urlString + "\" is blacklisted.");
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} catch (MalformedURLException e) {
			logger.error("Url is Malformed, cannot check if \""+urlString + "\" is blacklisted.");
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	private boolean oldLink(Date date) {
		final int ALLOWED_HOURS = 2; 
		final int MILLI_TO_HOUR = 1000 * 60 * 60;
		Date now = new Date(System.currentTimeMillis());
		
	    int spent_hours = (int) (now.getTime() - date.getTime()) / MILLI_TO_HOUR;
	    // Return true if the link was checked earlier than
	    // two hours ago
	    return spent_hours > ALLOWED_HOURS;
	}
	

	
	
}
