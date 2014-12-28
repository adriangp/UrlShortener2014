package urlshortener2014.goldenbrown.platformidentifier;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlatformIdentifierService {
	
	//http://hc.apache.org/httpclient-3.x/tutorial.html
	@RequestMapping(value = "/platformidentifier", method = RequestMethod.POST)
	public static PlatformIdentity getPlatformAndOs(@RequestParam("useragentstring") String userAgentString){
		PlatformIdentity pi = new PlatformIdentity(userAgentString);
		return pi;
	}


}
