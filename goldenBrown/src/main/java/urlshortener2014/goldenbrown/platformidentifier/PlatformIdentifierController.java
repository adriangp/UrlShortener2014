package urlshortener2014.goldenbrown.platformidentifier;


import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import urlshortener2014.goldenbrown.blacklist.BlackListController;

/**
* Class that consist of read the headers User-Agent of the request HTTP and about it,
* clasify navigator and platform of the click. For that, we use the library UserAgentUtils
* for fill better information of the request.
* @author: Jorge,Javi,Gabi
* @version: 08/01/2015
*/
@RestController
public class PlatformIdentifierController {
	
	private static final Logger logger = LoggerFactory.getLogger(PlatformIdentifierController.class);
	
	/**
	* Main method that gets the request of the user and identifier the navigator and platform
	* of the user. For this, we use the class PlatformIdentity that abstract the process of
	* get the information from the User-Agent.
	* @param us contain the User-Agent
	* @param request of the user
	* @return
	*/
	@RequestMapping(value = "/platformidentifier", method = RequestMethod.GET)
	public ResponseEntity<PlatformIdentity> getPlatform(
			@RequestParam(value = "us", required = true) String us,
			HttpServletRequest request)
	{
		try{
			PlatformIdentity pi = new PlatformIdentity(us);
			logger.info("User Agent Information: OS=\"" + pi.getOs() + "\" , Browser=\"" + pi.getBrowser() + "\" , Version=\"" +pi.getVersion()+"\"");
			return new ResponseEntity<PlatformIdentity>(pi, HttpStatus.OK);
		}
		catch(IllegalArgumentException e){
			logger.error("Bad Request, User-Agent cannot be read");
			return new ResponseEntity<PlatformIdentity>(HttpStatus.BAD_REQUEST);
		}
		
		
	}


}
