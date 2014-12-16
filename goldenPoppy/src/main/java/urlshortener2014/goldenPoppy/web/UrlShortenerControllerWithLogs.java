package urlshortener2014.goldenPoppy.web;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import urlshortener2014.common.domain.ShortURL;
import urlshortener2014.common.repository.ShortURLRepository;
import urlshortener2014.common.web.UrlShortenerController;
import urlshortener2014.goldenPoppy.intesicial.IntersicialEndPoint;

@RestController
public class UrlShortenerControllerWithLogs extends UrlShortenerController {

	private static final Logger logger = LoggerFactory.getLogger(UrlShortenerControllerWithLogs.class);
	
	@Autowired
	private ShortURLRepository shortURLRepository;
	
	public ResponseEntity<?> redirectTo(@PathVariable String id, 
			HttpServletRequest request) {
		logger.info("Requested redirection with hash "+id);
		if(shortURLRepository.findByKey(id).getSponsor() == null){
			return super.redirectTo(id, request);
		}else{
			IntersicialEndPoint inter = new IntersicialEndPoint(id,request);
			inter.redireccionarPubli();
			return null;
		}
	}

	public ResponseEntity<ShortURL> shortener(@RequestParam("url") String url,
			@RequestParam(value = "sponsor", required = false) String sponsor,
			@RequestParam(value = "brand", required = false) String brand,
			HttpServletRequest request) {
		logger.info("Requested new short for uri "+url);
		return super.shortener(url, sponsor, brand, request);
	}
	
	/**
	 * Crea una nueva URL corta que apunta a la URL sin publicidad
	 * @param sUrl
	 * @param sponsor
	 * @param request
	 * @return
	 */
	public ResponseEntity<ShortURL> intersicial(@RequestParam("shorturl") String sUrl,
			@RequestParam(value = "sponsor", required = false) String sponsor,
			HttpServletRequest request){
		return shortener(sUrl,sponsor,null,request);
	}
}