package urlshortener2014.demo.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import urlshortener2014.common.domain.ShortURL;
import urlshortener2014.common.web.UrlShortenerController;

@RestController
public class UrlShortenerControllerWithLogs extends UrlShortenerController {

	private static final Logger logger = LoggerFactory.getLogger(UrlShortenerControllerWithLogs.class);
	
	public ResponseEntity<?> redirectTo(@PathVariable String id) {
		logger.info("Requested redirection with hash "+id);
		return super.redirectTo(id);
	}

	public ResponseEntity<ShortURL> shortener(
			@RequestParam MultiValueMap<String, String> form) {
		logger.info("Requested new short for uri "+form.getFirst("url"));
		return super.shortener(form);
	}
}

