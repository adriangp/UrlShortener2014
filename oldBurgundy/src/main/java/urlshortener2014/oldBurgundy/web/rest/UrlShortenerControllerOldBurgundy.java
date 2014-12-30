package urlshortener2014.oldBurgundy.web.rest;

import java.net.MalformedURLException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import urlshortener2014.common.domain.ShortURL;
import urlshortener2014.common.web.UrlShortenerController;
import urlshortener2014.oldBurgundy.repository.sponsor.SponsorWork;

@RestController
public class UrlShortenerControllerOldBurgundy extends UrlShortenerController {

	private static final Logger logger = LoggerFactory.getLogger(UrlShortenerControllerOldBurgundy.class);
	
	public ResponseEntity<?> redirectTo(@PathVariable String id, 
			HttpServletRequest request) {
		logger.info("Request redirectionfirts with hash " + id);
		if (shortURLRepository.findByKey(id)==null)
			return new ResponseEntity<>("<html><body>Bad Request: Poner bonito</body></html>", HttpStatus.BAD_REQUEST); 
		ResponseEntity<?> response = super.redirectTo(id, request);
		SponsorWork work = new SponsorWork(id,shortURLRepository.findByKey(id).getSponsor());
		clickRepository.clicksByHash(id);
	//	this.worksRepositorySponsor.addIncomingWork(work);
		if (shortURLRepository.findByKey(id).getSponsor()==null || shortURLRepository.findByKey(id).getSponsor().isEmpty())
			return new ResponseEntity<String>("<html><body><iframe src="+"www.unizar.es"+"></iframe></body></html>", HttpStatus.OK);
			
		return new ResponseEntity<String>("<html><body><iframe src="+shortURLRepository.findByKey(id).getSponsor()+"></iframe></body></html>", HttpStatus.OK);
	}

	public ResponseEntity<ShortURL> shortener(@RequestParam("url") String url,
			@RequestParam(value = "sponsor", required = false) String sponsor,
			@RequestParam(value = "brand", required = false) String brand,
			HttpServletRequest request) {
		logger.info("Requested new short for uri "+url);
		return super.shortener(url, sponsor, brand, request);
	}
	
	@RequestMapping(value = "/link/{id}", method = RequestMethod.POST)
	public ResponseEntity<?> shortener2(@PathVariable int id, @RequestParam("url") String url,
			@RequestParam(value = "sponsor", required = false) String sponsor,
			@RequestParam(value = "brand", required = false) String brand,
			HttpServletRequest request) {
		
		logger.info("Requested new short for id: " + id + " - uri: " + url + " - sponsor: " + sponsor);
		
		ResponseEntity<ShortURL> response = super.shortener(url, sponsor, brand, request);
		logger.info("short url for id: " + id + " - uri: " + response.getBody().getHash() + response.getBody().getTarget() + " - sponsor: " + sponsor);

		(new RestTemplate()).postForEntity("http://localhost:8080/csv/rest/" + id, response.getBody().getUri().toString(), null);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}

