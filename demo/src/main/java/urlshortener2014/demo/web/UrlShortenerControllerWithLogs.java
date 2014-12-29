package urlshortener2014.demo.web;

import java.sql.Date;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import urlshortener2014.common.domain.Click;
import urlshortener2014.common.domain.ShortURL;
import urlshortener2014.common.web.UrlShortenerController;

@RestController
public class UrlShortenerControllerWithLogs extends UrlShortenerController {

	private static final Logger logger = LoggerFactory.getLogger(UrlShortenerControllerWithLogs.class);
	
	public ResponseEntity<?> redirectTo(@PathVariable String id, 
			HttpServletRequest request) {
		String agent = request.getHeader("User-Agent");
		String navegador="",SO="";
		if(agent.indexOf("Chrome")!=-1) navegador="Chrome";
		else if(agent.indexOf("Firefox")!=-1) navegador="Firefox";
		else if(agent.indexOf("Safari")!=-1) navegador="Safari";
		
		if(agent.indexOf("Windows")!=-1) SO="Windows";
		else if(agent.indexOf("Linux")!=-1) SO="Linux";
		Click cl = new Click(null, hash, new Date(System.currentTimeMillis()),
				null, null, null, ip, null);
	
		logger.info("Requested redirection with hash "+id);
		// Guardar en un objeto la llamada al padre, guardarme en una lista la consulta
		// a los Cliks, y quedarme con el ultimo con la IP del request, modificar el
		// click con el navegador y SO, actualizar BD y return
		return super.redirectTo(id, request);
	}

	public ResponseEntity<ShortURL> shortener(@RequestParam("url") String url,
			@RequestParam(value = "sponsor", required = false) String sponsor,
			@RequestParam(value = "brand", required = false) String brand,
			HttpServletRequest request) {
		logger.info("Requested new short for uri "+url);
		return super.shortener(url, sponsor, brand, request);
	}
}

