package urlshortener2014.richcarmine.web;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityLinks;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.client.RestTemplate;
import urlshortener2014.common.domain.ShortURL;
import urlshortener2014.common.repository.ShortURLRepository;
import urlshortener2014.common.web.UrlShortenerController;

import java.awt.image.BufferedImage;
import java.util.UUID;

@RestController
public class UrlShortenerControllerWithLogs extends UrlShortenerController {

	private static final Logger logger = LoggerFactory.getLogger(UrlShortenerControllerWithLogs.class);

    @Autowired
    private ShortURLRepository shortURLRepository;

    @Autowired
    EntityLinks entityLinks;
	
	public ResponseEntity<?> redirectTo(@PathVariable String id, 
			HttpServletRequest request) {
		logger.info("Requested redirection with hash "+id);
		return super.redirectTo(id, request);
	}

	public ResponseEntity<ShortURL> shortener(@RequestParam("url") String url,
			@RequestParam(value = "sponsor", required = false) String sponsor,
			@RequestParam(value = "brand", required = false) String brand,
			HttpServletRequest request) {
		logger.info("Requested new short for uri "+url);
		return super.shortener(url, sponsor, brand, request);
	}

    @RequestMapping(value = "/qr", method = RequestMethod.POST)
    public ResponseEntity<ShortURL> QRrize(@RequestParam("url") String url,
           @RequestParam(value = "sponsor", required = false) String sponsor,
           @RequestParam(value = "brand", required = false) String brand,
           HttpServletRequest request) {
        logger.info("Requested new short with QR code for uri "+url);
        return super.shortener(url, sponsor, brand, request);
    }

    @RequestMapping(value= "/qr{id}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> redirectQR(@PathVariable String id,
           HttpServletRequest request) {
        logger.info("Requested qr: qr"+id);
        ShortURL su = shortURLRepository.findByKey(id);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        System.out.println("\n"+su.getUri()+"\n");
        //TODO improve this thing
        String url = "https://chart.googleapis.com/chart?cht=qr&chs=300x300&chl=http://10.3.14.76:8080/l"+su.getHash()+"&choe=UTF-8";
        ResponseEntity<?> re = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                byte[].class);

        return new ResponseEntity<>((byte[]) re.getBody(),headers,HttpStatus.CREATED);
    }
}

