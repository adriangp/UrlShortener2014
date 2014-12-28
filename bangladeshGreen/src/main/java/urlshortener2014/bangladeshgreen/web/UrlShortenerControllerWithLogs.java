package urlshortener2014.bangladeshgreen.web;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import urlshortener2014.common.domain.ShortURL;
import urlshortener2014.common.repository.ShortURLRepository;
import urlshortener2014.common.web.UrlShortenerController;

@RestController
public class UrlShortenerControllerWithLogs extends UrlShortenerController {

	@Autowired
	private ShortURLRepository SURLR;
	private static final Logger logger = LoggerFactory.getLogger(UrlShortenerControllerWithLogs.class);
	
	@Override
	public ResponseEntity<?> redirectTo(@PathVariable String id) {
		logger.info("Requested redirection with hash "+id);
		return super.redirectTo(id);
	}

	@Override
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<ShortURL> shortener(@RequestParam("url") String url,
			@RequestParam(value = "sponsor", required = false) String sponsor,
			@RequestParam(value = "brand", required = false) String brand,
			HttpServletRequest request) {
		logger.info("Requested new short for uri "+"url");
		ResponseEntity<ShortURL>su=super.shortener(url, brand, brand, request);
		//comprobar si es segura
		Client c = ClientBuilder.newClient();
		url=parse(url);
		Response response = c
				.target("https://sb-ssl.google.com/safebrowsing/api/lookup?client=Roberto&key=AIzaSyBbjDCPwK13dOYioVf6Cp9_lrFZ_MOEFbU&appver=1.5.2&pver=3.1&url="+url)
				.request(MediaType.TEXT_HTML)
				.get();
		
		if(response.getStatus()==204){
			SURLR.mark(su.getBody(), false);//marcar como no segura
		}
		
		return su;
	}
	
	
	private static String parse(String a) {
		String res="";
		for(int i=0;i<a.length();i++){
			switch(a.charAt(i)){
				case ':':
					res=res+"%3A";
					break;
				case '/':
					res=res+"%2F";
					break;
				case ' ':
					res=res+"%20";
					break;
				case '?':
					res=res+"%3F";
					break;
				case '<':
					res=res+"%3C";
					break;
				case '>':
					res=res+"%3E";
					break;
				case '%':
					res=res+"%25";
					break;
				case '#':
					res=res+"%23";
					break;
				case ';':
					res=res+"%3B";
					break;
				case '|':
					res=res+"%7C";
					break;
				case '&':
					res=res+"%26";
					break;
				default:
					res=res+a.charAt(i);
					break;
			}
		}
		return res;
	}
}
