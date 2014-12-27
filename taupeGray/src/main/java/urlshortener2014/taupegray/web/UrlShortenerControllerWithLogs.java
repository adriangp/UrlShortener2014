package urlshortener2014.taupegray.web;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import urlshortener2014.common.domain.ShortURL;
import urlshortener2014.common.repository.ShortURLRepository;
import urlshortener2014.common.web.UrlShortenerController;
import urlshortener2014.taupegray.qr.QRFetcher;
import urlshortener2014.taupegray.safebrowsing.SafeBrowsing;
import urlshortener2014.taupegray.sponsor.WebToStringWrapper;

@RestController
public class UrlShortenerControllerWithLogs extends UrlShortenerController {

	@Autowired
	private ShortURLRepository shortURLRepository;
	
	private static final Logger logger = LoggerFactory
			.getLogger(UrlShortenerControllerWithLogs.class);

	public ResponseEntity<?> redirectTo(@PathVariable String id,
			HttpServletRequest request) {
		logger.info("Requested redirection with hash " + id);
		return super.redirectTo(id, request);
	}

	protected ResponseEntity<?> createSuccessfulRedirectToResponse(ShortURL l) {
		boolean safe = SafeBrowsing.isSafe(l.getTarget());
		if (l.getSponsor() == null) {
			if(safe) {
				HttpHeaders h = new HttpHeaders();
				h.setLocation(URI.create(l.getTarget()));
				
				return new ResponseEntity<>(h, HttpStatus.valueOf(l.getMode()));
			}
			else {
				return new ResponseEntity<>("WarningWebpage", HttpStatus.OK);
			}
		}
		else {
			ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			HttpServletRequest request = sra.getRequest();
			request.setAttribute("safe", safe);
			request.setAttribute("id", l.getHash());
			//request.setAttribute("sponsorsafe", SafeBrowsing.isSafe(l.getSponsor())); //Prueba para avisar que el sponsor no es seguro
			return new ResponseEntity<>(new WebToStringWrapper("/WEB-INF/sponsor.jsp",request,sra.getResponse()).getContent(), HttpStatus.OK);
		}
	}

	public ResponseEntity<ShortURL> shortener(@RequestParam("url") String url,
			@RequestParam(value = "sponsor", required = false) String sponsor,
			@RequestParam(value = "brand", required = false) String brand,
			HttpServletRequest request) {
		logger.info("Requested new short for uri " + url);

		UrlValidator urlValidator = new UrlValidator(new String[] { "http",
				"https" });

		if (sponsor == null || sponsor.length() == 0
				|| urlValidator.isValid(sponsor)) {
			if (sponsor.length() == 0) {
				sponsor = null;
			}
			logger.info("uri sponsor: " + sponsor);

			return super.shortener(url, sponsor, null, request);
		} else {
			logger.info("incorrect sponsor: " + sponsor);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/qr{id}", method = RequestMethod.GET)
	public ResponseEntity<?> QRGenerator(@PathVariable String id,
			HttpServletRequest request) {
		logger.info("Requested QR for short uri " + id);
		ShortURL l = shortURLRepository.findByKey(id);
		
		if (l != null) {
			return QRFetcher.FetchQR(linkTo(
					methodOn(UrlShortenerController.class).redirectTo(
							id, null)).toUri());
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
}
