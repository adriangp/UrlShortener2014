package urlshortener2014.mediumcandy.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

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
		String restURI = linkTo(methodOn(UrlShortenerControllerWithLogs.class).
                shortenerIfReachable(url, null, null, null)).toString();

		RestTemplate restTemplate = new RestTemplate();

		su = restTemplate.postForObject(restURI, null, ShortURL.class);
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
