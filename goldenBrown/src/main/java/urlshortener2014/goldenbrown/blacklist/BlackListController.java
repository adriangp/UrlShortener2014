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

@RestController
@RequestMapping("/blacklist")
public class BlackListController {
	
	@Autowired
	BlackListService blackListService;
	
	private static final Logger logger = LoggerFactory.getLogger(BlackListController.class);
	
	/**
	 * 
	 * @param urlString: url introduced manually by the user
	 * 			It represents an accessible URL
	 * @return
	 */
	@RequestMapping(value = "/onshortener", method = RequestMethod.GET)
	public ResponseEntity<?> onShortener(@RequestParam("url") String urlString){
		return askBlackListService(urlString);
	}

	
	
	/**
	 * 
	 * @param urlString: url pointed by some of our links
	 * @return
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
		    	return new ResponseEntity<>(HttpStatus.OK);
		    }
		    else{
		    	return new ResponseEntity<>(HttpStatus.LOCKED);
		    }
		}
		catch(IllegalArgumentException e){
			System.err.println(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} catch (MalformedURLException e) {
			System.err.println(e.getMessage());
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
