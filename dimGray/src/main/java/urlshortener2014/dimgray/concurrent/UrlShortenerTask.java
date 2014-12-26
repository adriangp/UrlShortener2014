package urlshortener2014.dimgray.concurrent;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import urlshortener2014.common.domain.ShortURL;
import urlshortener2014.dimgray.domain.UrlPair;
import urlshortener2014.dimgray.web.UrlShortenerControllerWithLogs;;

public class UrlShortenerTask extends RequestContextAwareCallable<UrlPair>{

	private String url,sponsor,brand;
	private HttpServletRequest request;
	private UrlShortenerControllerWithLogs uscw;

	public UrlShortenerTask(String url,HttpServletRequest request, String sponsor, 
			String brand,UrlShortenerControllerWithLogs uscw) {
		this.url = url;
		this.request = request;
		this.sponsor = sponsor;
		this.brand = brand;
		this.uscw = uscw;
	}

	@Override
	public UrlPair onCall() {
		ResponseEntity<ShortURL> re = uscw.shortener(url, sponsor, brand,request);
		if(re.getStatusCode() == HttpStatus.BAD_REQUEST){
			return new UrlPair(url,null);
		}
		return new UrlPair(url,re.getBody().getUri());
	}



}
