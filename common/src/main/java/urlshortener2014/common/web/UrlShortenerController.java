package urlshortener2014.common.web;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.Date;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityLinks;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import urlshortener2014.common.domain.ShortURL;
import urlshortener2014.common.repository.ShortURLRepository;

import com.google.common.hash.Hashing;

@RestController
public class UrlShortenerController {

	@Autowired
	private ShortURLRepository repository;

	@Autowired
	EntityLinks entityLinks;

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> redirectTo(@PathVariable String id) {
		ShortURL l = repository.findByKey(id);
		if (l != null) {
			HttpHeaders h = new HttpHeaders();
			h.setLocation(URI.create(l.getTarget()));
			return new ResponseEntity<>(h, HttpStatus.valueOf(l.getMode()));
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<ShortURL> shortener(
			@RequestParam MultiValueMap<String, String> form) {
		String url = form.getFirst("url");
		UrlValidator urlValidator = new UrlValidator(new String[] { "http",
				"https" });
		if (urlValidator.isValid(url)) {
			String id = Hashing.murmur3_32()
					.hashString(url, StandardCharsets.UTF_8).toString();
			ShortURL su = new ShortURL(id, url, linkTo(methodOn(UrlShortenerController.class).redirectTo(id))
					.toUri(), new Date(System.currentTimeMillis()), null, HttpStatus.TEMPORARY_REDIRECT.value());
			su = repository.save(su);
			HttpHeaders h = new HttpHeaders();
			h.setLocation(su.getUri());
			return new ResponseEntity<>(su, h, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
}
