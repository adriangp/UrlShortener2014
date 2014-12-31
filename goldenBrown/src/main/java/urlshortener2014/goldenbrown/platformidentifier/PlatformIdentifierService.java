package urlshortener2014.goldenbrown.platformidentifier;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import urlshortener2014.common.domain.ShortURL;

@RestController
public class PlatformIdentifierService {
	
//	@RequestMapping(value = "/platformidentifier/{useragentstring}", method = RequestMethod.GET)
//	public ResponseEntity<PlatformIdentity> getPlatformAndOs(@RequestParam("useragentstring") String userAgentString){
//		PlatformIdentity pi = new PlatformIdentity(userAgentString);
//		return new ResponseEntity<>(pi, HttpStatus.CREATED);
//	}
	
	@RequestMapping(value = "/platformidentifier/", method = RequestMethod.GET)
	public ResponseEntity<PlatformIdentity> getPlatform(
			@RequestParam(value = "us", required = true) String us,
			HttpServletRequest request)
	{
		System.err.println("us: "+us);
		try{
			PlatformIdentity pi = new PlatformIdentity(us);
			return new ResponseEntity<PlatformIdentity>(pi, HttpStatus.OK);
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}


}
