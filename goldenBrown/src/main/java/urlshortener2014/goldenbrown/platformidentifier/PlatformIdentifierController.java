package urlshortener2014.goldenbrown.platformidentifier;


import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class PlatformIdentifierController {
		
	@RequestMapping(value = "/platformidentifier", method = RequestMethod.GET)
	public ResponseEntity<PlatformIdentity> getPlatform(
			@RequestParam(value = "us", required = true) String us,
			HttpServletRequest request)
	{
		try{
			PlatformIdentity pi = new PlatformIdentity(us);
			return new ResponseEntity<PlatformIdentity>(pi, HttpStatus.OK);
		}
		catch(IllegalArgumentException e){
			return new ResponseEntity<PlatformIdentity>(HttpStatus.BAD_REQUEST);
		}
		
		
	}


}
