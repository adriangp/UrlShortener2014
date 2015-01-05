package urlshortener2014.goldenPoppy.massiveLoad;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import urlshortener2014.goldenPoppy.web.UrlShortenerControllerWithLogs;

public class Load implements Runnable{

	private List<Content> longUrls;
	private List<String> shortUrls;
	private UrlShortenerControllerWithLogs controller;
	private HttpServletRequest request;
	
	public Load(ArrayList<Content> longs, ArrayList<String> shorts, 
			UrlShortenerControllerWithLogs u, HttpServletRequest request){
		this.longUrls = longs;
		this.shortUrls = shorts;
		this.controller = u;
		this.request = request;
	}
	
	@Override
	public void run() {
		ResponseEntity<?> resp = null;
		for (Content c : longUrls){
			resp = controller.shortener(c.getURL(), c.getSponsor(), null, request);
			if (resp.getStatusCode() == HttpStatus.CREATED)
				shortUrls.add(c.getId(), resp.getHeaders().getLocation().toString());
		}
	}	
}