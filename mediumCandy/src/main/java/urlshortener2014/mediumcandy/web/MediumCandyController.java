package urlshortener2014.mediumcandy.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import urlshortener2014.common.domain.ShortURL;

@RestController
public class MediumCandyController {

	/**
	 * Shortens a given URL if this URL is reachable via HTTP.
	 */
	@RequestMapping(value = "/mediumcandy/linkreachable", method = RequestMethod.POST)
	public ResponseEntity<ShortURL> shortenerIfReachable(@RequestParam("url") String url,
			@RequestParam(value = "sponsor", required = false) String sponsor,
			@RequestParam(value = "brand", required = false) String brand,
			HttpServletRequest request) {
		ShortURL su = null;
		
		/*------------------------------------------------------------
		 * CONSUMING REST Service
		 ------------------------------------------------------------*/
		Map<String, String> vars = new HashMap<String, String>();
	    vars.put("url", url);
	    
		RestTemplate restTemplate = new RestTemplate();
		su = restTemplate.postForObject("http://localhost:8080/linkreachable", null, ShortURL.class, vars);
		/*-----------------------------------------------------------*/
		
		if (su != null) {
			HttpHeaders h = new HttpHeaders();
			h.setLocation(su.getUri());
			return new ResponseEntity<>(su, h, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
}
