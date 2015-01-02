package urlshortener2014.goldenbrown.blacklist;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/blacklist")
public class BlackListService {
	
	private static final Logger logger = LoggerFactory.getLogger(BlackListService.class);
	
	/**
	 * 
	 * @param urlString: url introduced manually by the user
	 * 			It represents an accessible URL
	 * @return
	 */
	@RequestMapping(value = "/onshortener", method = RequestMethod.GET)
	public ResponseEntity<?> onShortener(@RequestParam("url") String urlString){
		URL url = null;
		try{
			// Sanitize URL (If it's not valid, it will throw a MalformedURLException)
			url = new URL(urlString);
		    boolean blacklisted = isBlackListed(url);
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
		} finally{
			
		}
	}
	
	/**
	 * 
	 * @param urlString: url pointed by some of our links
	 * @return
	 */
	@RequestMapping(value = "/onredirectto", method = RequestMethod.GET)
	public ResponseEntity<?> onRedirectTo(@RequestParam("url") String urlString){
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
	
	private boolean isBlackListed(URL url){
		String host = "", domain = "";
		boolean blacklisted = false;
		final String[] antispamSites = {"zen.spamhaus.org",
										"multi.surbl.org",
										"black.uribl.com"};
		
		
		host = url.getHost();
		//TODO: Remove www. from domain (but not from www.com)
		host = host.replace("www.", "");
		//TODO: Implement cache
		
		for (String site: antispamSites){
			domain = host+"."+site+".";
			//TODO: Do DNS query
			try {
				InetAddress ip = InetAddress.getByName(domain);
				logger.info("ip="+ip);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
		return blacklisted;
	}
	
	
}
