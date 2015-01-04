package urlshortener2014.mediumcandy.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import urlshortener2014.common.domain.ShortURL;

@RestController
public class MediumCandyController {

    @RequestMapping( value = "/mediumcandy/link", method = RequestMethod.POST )
    public ResponseEntity<ShortURL> shortener(@RequestParam("url") String url,
			@RequestParam(value = "sponsor", required = false) String sponsor,
			@RequestParam(value = "brand", required = false) String brand,
			HttpServletRequest request) {
    	
    	// to be implemented...
    	
        return null;
    }
}
