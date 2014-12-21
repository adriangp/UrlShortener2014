package urlshortener2014.goldenPoppy.web;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import urlshortener2014.common.domain.ShortURL;
import urlshortener2014.common.repository.ShortURLRepository;
import urlshortener2014.common.web.UrlShortenerController;
import urlshortener2014.goldenPoppy.intesicial.IntersicialEndPoint;
import urlshortener2014.goldenPoppy.isAlive.*;

@RestController
public class UrlShortenerControllerWithLogs extends UrlShortenerController {

	private static final Logger logger = LoggerFactory.getLogger(UrlShortenerControllerWithLogs.class);
	
	@Autowired
	private ShortURLRepository shortURLRepository;
	
	@Autowired
	private IntersicialEndPoint inter;

	public ResponseEntity<?> redirectTo(@PathVariable String id, 
			HttpServletRequest request) {
		logger.info("Requested redirection with hash "+id);
		if(shortURLRepository.findByKey(id).getSponsor() == null){
			return super.redirectTo(id, request);
		}else{
			return inter.redireccionarPubli(id);
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
	
	@RequestMapping(value = "/isalive", method = RequestMethod.GET)
	@MessageMapping("/isalive")
    @SendTo("/topic/isalive")
    public Response isalive(URL url) throws Exception {
    	// TODO Implementar el timer
    	
    	HttpClient client = HttpClientBuilder.create().build();
    	HttpHead request = new HttpHead(url.getUrl());
    	HttpResponse response = client.execute(request);
    	
    	int resultado = response.getStatusLine().getStatusCode();
    	String phrase = response.getStatusLine().getReasonPhrase();
    	
    	
        return new Response(resultado + " " + phrase);
    }
}