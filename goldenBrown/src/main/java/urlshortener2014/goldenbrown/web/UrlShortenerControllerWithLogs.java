package urlshortener2014.goldenbrown.web;

import java.sql.Date;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import urlshortener2014.common.domain.Click;
import urlshortener2014.common.domain.ShortURL;
import urlshortener2014.common.repository.ClickRepository;
import urlshortener2014.common.repository.ClickRepositoryImpl;
import urlshortener2014.common.repository.ShortURLRepository;
import urlshortener2014.common.web.UrlShortenerController;
import urlshortener2014.goldenbrown.platformidentifier.PlatformIdentifierService;
import urlshortener2014.goldenbrown.platformidentifier.PlatformIdentity;
import urlshortener2014.goldenbrown.reachableurl.ReachableURLService;

@RestController
public class UrlShortenerControllerWithLogs extends UrlShortenerController {

	private static final Logger logger = LoggerFactory.getLogger(UrlShortenerControllerWithLogs.class);
	
	@Autowired
	private ShortURLRepository shortURLRepository;
	
	@Autowired
	private ClickRepository clickRepository;
	
//	@Override
//	public ResponseEntity<?> redirectTo(@PathVariable String id, 
//			HttpServletRequest request) {
//		logger.info("Requested redirection with hash "+id);
//		return super.redirectTo(id, request);
//	}
	@Override
	@RequestMapping(value = "/l{id}", method = RequestMethod.GET)
	public ResponseEntity<?> redirectTo(@PathVariable String id,
			HttpServletRequest request) {
		ShortURL l = shortURLRepository.findByKey(id);
		// TODO: Hacer una petición al servicio PlatformIdentifierService
		String browser = "", platform = "";	

		if (l != null) {
			createAndSaveClick(id, extractIP(request), browser, platform);
			return createSuccessfulRedirectToResponse(l);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@Override
	public ResponseEntity<ShortURL> shortener(@RequestParam("url") String url,
			@RequestParam(value = "sponsor", required = false) String sponsor,
			@RequestParam(value = "brand", required = false) String brand,
			HttpServletRequest request) {
		logger.info("Requested new short for uri "+url);
		return super.shortener(url, sponsor, brand, request);
//		if(URLAlcanzable.isURLachievable(url)){
//			return super.shortener(url, sponsor, brand, request);
//		}
//		return null;
	}
	
	protected void createAndSaveClick(String hash, String ip, String browser, String platform) {
		Click cl = new Click(null, hash, new Date(System.currentTimeMillis()),
				null, browser, platform, ip, null);
		// OPCION 1
//		ClickRepositoryImpl cri = new ClickRepositoryImpl();
//		cri.save(cl);
		// OPCION 2
		clickRepository.save(cl);
	}
	
	private String extractIP(HttpServletRequest request) {
		return request.getRemoteAddr();
	}
}

