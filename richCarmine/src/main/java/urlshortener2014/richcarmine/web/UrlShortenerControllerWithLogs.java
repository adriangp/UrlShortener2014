package urlshortener2014.richcarmine.web;


import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import javax.servlet.http.HttpServletRequest;

import com.google.common.hash.Hashing;
import org.apache.commons.validator.routines.UrlValidator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.ArrayList;
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
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        //TODO improve this thing
        String uri  = linkTo(methodOn(UrlShortenerController.class).redirectTo(id, null)).toUri().toString();
        String url = "https://chart.googleapis.com/chart?cht=qr&chs=300x300&chl="+uri+"&choe=UTF-8";
        ResponseEntity<?> re = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                byte[].class);

        return new ResponseEntity<>((byte[]) re.getBody(),headers,HttpStatus.CREATED);
    }

    private String getLocationByIP(String ip){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        ArrayList<MediaType> acceptableMedia = new ArrayList<>();
        acceptableMedia.add(MediaType.APPLICATION_JSON);
        headers.setAccept(acceptableMedia);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String url = "http://www.telize.com/geoip/"+ip;
        ResponseEntity<String> re = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class);
        JSONObject json = new JSONObject(re.getBody());
        String country;
        try{
            logger.info(json.getString("country"));
            country = json.getString("country");
        }catch(JSONException e){
            country = null;
        }
        return country;
    }

    @Override
    protected ShortURL createAndSaveIfValid(String url, String sponsor,
                                            String brand, String owner, String ip) {
        UrlValidator urlValidator = new UrlValidator(new String[] { "http",
                "https" });
        if (urlValidator.isValid(url)) {
            String id = Hashing.murmur3_32()
                    .hashString(url, StandardCharsets.UTF_8).toString();
            ShortURL su = new ShortURL(id, url,
                    linkTo(
                            methodOn(UrlShortenerController.class).redirectTo(
                                    id, null)).toUri(), sponsor, new Date(
                    System.currentTimeMillis()), owner,
                    HttpStatus.TEMPORARY_REDIRECT.value(), true, ip, getLocationByIP(ip));
            return shortURLRepository.save(su);
        } else {
            return null;
        }
    }
}

