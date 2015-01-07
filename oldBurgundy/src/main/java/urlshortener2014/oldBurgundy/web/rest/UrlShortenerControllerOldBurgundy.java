package urlshortener2014.oldBurgundy.web.rest;

import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import urlshortener2014.common.domain.ShortURL;
import urlshortener2014.common.web.UrlShortenerController;
import urlshortener2014.oldBurgundy.repository.sponsor.SponsorWork;
import urlshortener2014.oldBurgundy.repository.sponsor.WorksRepositorySponsor;
import urlshortener2014.oldBurgundy.web.errorcontroler.ErrorMvcAutoConfiguration;

@RestController
public class UrlShortenerControllerOldBurgundy extends UrlShortenerController {
	
	@Autowired
	private WorksRepositorySponsor worksRepositorySponsor;
	
	@Autowired
	private String hostCsv;
	
	private static final Logger logger = LoggerFactory.getLogger(UrlShortenerControllerOldBurgundy.class);
	
	public ResponseEntity<?> redirectTo(@PathVariable String id, HttpServletRequest request) {
		logger.info("Request redirection firts with hash: '" + id + "'");
		
		ShortURL shortURL = shortURLRepository.findByKey(id);
		
		if (shortURL == null){
			logger.info("Not found hash: '" + id + "'");
			
			return ErrorMvcAutoConfiguration.responseError(HttpStatus.NOT_FOUND);
		}
	
		String uri = shortURL.getTarget();
		
		SponsorWork work = new SponsorWork(uri, id);
		worksRepositorySponsor.addIncomingWork(work);

		VelocityContext context = new VelocityContext();
		StringWriter writer = new StringWriter();
		
		if (shortURL.getSponsor() == null || shortURL.getSponsor().isEmpty()){
			context.put("sponsor", "default_sponsor.html");
		}
		else{
			context.put("sponsor", shortURL.getSponsor());
		}
		
		context.put("shortURL", shortURL.getHash());
		Velocity.mergeTemplate("sponsor.vm", "ISO-8859-1", context, writer);
		
		HttpHeaders h = new HttpHeaders();
		h.setContentType(MediaType.TEXT_HTML);
		return new ResponseEntity<>(writer.toString(), h, HttpStatus.OK);
	}

	public ResponseEntity<ShortURL> shortener(@RequestParam("url") String url,
			@RequestParam(value = "sponsor", required = false) String sponsor,
			@RequestParam(value = "brand", required = false) String brand,
			HttpServletRequest request) {
		logger.info("Requested new short for uri " + url);
		return super.shortener(url, sponsor, brand, request);
	}
	
	@RequestMapping(value = "/link/{id}", method = RequestMethod.POST)
	public ResponseEntity<?> shortener2(@PathVariable int id, @RequestParam("url") String url,
			@RequestParam(value = "sponsor", required = false) String sponsor,
			@RequestParam(value = "brand", required = false) String brand,
			HttpServletRequest request) {
		
		logger.info("Requested new short (shortener2) for id: " + id + " - uri: " + url + " - sponsor: " + sponsor);
		
		ResponseEntity<ShortURL> response = super.shortener(url, sponsor, brand, request);

		try{
			(new RestTemplate()).postForEntity(hostCsv + "/csv/rest/" + id, response.getBody().getUri().toString(), null);
		}
		catch(HttpClientErrorException e){
		}
		catch(HttpServerErrorException e){
		}
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
}

