package urlshortener2014.goldenbrown.web;

import java.sql.Date;

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

import urlshortener2014.common.domain.Click;
import urlshortener2014.common.domain.ShortURL;
import urlshortener2014.common.web.UrlShortenerController;
import urlshortener2014.goldenbrown.platformidentifier.PlatformIdentity;
/**
 * Clase principal del proyecto. En esta clase se controla tanto la creacion de la URL acortado por medio
 * del metodo shortener, como la gestion de la redireccion con el click del usuario.
 * @author: Jorge,Javi,Gabi
 * @version: 08/01/2015
 */
@RestController
public class UrlShortenerControllerWithLogs extends UrlShortenerController {
	//Clase privada que sirve de logger para el debug de la aplicacion
	private static final Logger logger = LoggerFactory.getLogger(UrlShortenerControllerWithLogs.class);
	
	/**
	 * Metodo que sirve para gestionar todo lo referente al click del usuario sobre la URL acortada.
	 * En este metodo se realiza la identificacion de la plataforma y navegador del usuario por medio
	 * del User-Agent
	 * @param id de la URL acortada
	 * @param request objeto utilizado para saber la informacion del usuario por medio de su User-Agent
	 */
	@Override
	@RequestMapping(value = "/l{id}", method = RequestMethod.GET)
	public ResponseEntity<?> redirectTo(@PathVariable String id,
			HttpServletRequest request) {
		ShortURL l = shortURLRepository.findByKey(id);
		String useragentstring = "", browser = "", platform = "";	
		useragentstring = request.getHeader("User-Agent");
		final String uri = "http://localhost:8080/platformidentifier/?us={us}";
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<PlatformIdentity> response = restTemplate.getForEntity(
						uri,
						PlatformIdentity.class,
						useragentstring);
		
		if (response.getStatusCode().equals(HttpStatus.OK)){
			PlatformIdentity pi = response.getBody();
			if(pi.getVersion().equals("UNKNOWN")){
				browser = pi.getBrowser();
			}
			else{
				browser = pi.getBrowser() + " " + pi.getVersion();
			}
			platform = pi.getOs();
			logger.debug("Browser: "+ browser);
			logger.debug("Platform: "+platform);
		}
        if (l != null) {
			createAndSaveClick(id, extractIP(request), browser, platform);
			return createSuccessfulRedirectToResponse(l);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Metodo que sirve para acortar una URL pasada por el usuario, llama al metodo shortener del common
	 * @param url URL que el usuario quiere acortar
	 */
	@Override
	public ResponseEntity<ShortURL> shortener(@RequestParam("url") String url,
			@RequestParam(value = "sponsor", required = false) String sponsor,
			@RequestParam(value = "brand", required = false) String brand,
			HttpServletRequest request) {
		logger.info("Requested new short for uri "+url);
		return super.shortener(url, sponsor, brand, request);
	}
	
	/**
	 * Este metodo crea un objeto Click con la informacion recogida en el redirectTo para enviarla al
	 * clikRepository del common y guardar la informacion del click con la informacion del navegador
	 * y plataforma del usuario que ha pulsado en el click, ademas de la ip, la fecha y el id de la
	 * url.
	 * @param hash de la URL acortada que ha sido pulsada
	 * @param ip del usuario
	 * @param browser del usuario
	 * @param platform del usuario
	 */
	protected void createAndSaveClick(String hash, String ip, String browser, String platform) {
		Click cl = new Click(null, hash, new Date(System.currentTimeMillis()),
				null, browser, platform, ip, null);
		clickRepository.save(cl);
	}
}

