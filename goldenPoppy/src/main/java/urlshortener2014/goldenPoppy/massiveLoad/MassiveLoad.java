/*package urlshortener2014.goldenPoppy.massiveLoad;

import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean;

public class MassiveLoad {

	public static void main(String[] args){
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < 100; i++)
			list.add("hola: " + i);
		ThreadPoolExecutorFactoryBean factory = new ThreadPoolExecutorFactoryBean();
		factory.initialize();

		for (int i = 0; i < 10; i++){
			List<String> l = new ArrayList<String>(list.subList(i*10, i*10+10));
			factory.createThread(new Load(l)).run();
		}
	}
}



package urlshortener2014.goldenPoppy.web;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import urlshortener2014.common.domain.ShortURL;
import urlshortener2014.common.repository.ShortURLRepository;
import urlshortener2014.common.web.UrlShortenerController;
import urlshortener2014.goldenPoppy.intesicial.IntersicialEndPoint;
import urlshortener2014.goldenPoppy.isAlive.CompruebaUrl;
import urlshortener2014.goldenPoppy.isAlive.Response;
import urlshortener2014.goldenPoppy.isAlive.URL;
import urlshortener2014.goldenPoppy.massiveLoad.Load;

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
/*
	public ResponseEntity<ShortURL> intersicial(@RequestParam("shorturl") String sUrl,
			@RequestParam(value = "sponsor", required = false) String sponsor,
			HttpServletRequest request){
		return shortener(sUrl,sponsor,null,request);
	}
	
	@RequestMapping(value = "/isalive", method = RequestMethod.GET)
	@MessageMapping("/isalive")
    @SendTo("/topic/isalive")
    public Response isalive(URL url) throws Exception {

		ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(new CompruebaUrl(url));

        try {
        	String s = future.get(2, TimeUnit.SECONDS);
        	executor.shutdownNow();
            return new Response(s);
        } catch (TimeoutException e ) {
        	executor.shutdownNow();
        	return new Response("Le está costando responder...");
        } catch (Exception e){
        	executor.shutdownNow();
        	return new Response("La url no está viva");
        } 
    }
	
	@RequestMapping(value = "/massiveload", method = RequestMethod.GET)
	@MessageMapping("/massiveload")
	@SendTo("/topic/massiveload")
	public ResponseEntity<ShortURL> massiveLoad(File f) {
		ThreadPoolExecutorFactoryBean factory = new ThreadPoolExecutorFactoryBean();
		factory.createThread(new Load(new ArrayList<String>()));
		System.out.println(f.getPath());
		return new ResponseEntity<ShortURL>(HttpStatus.OK);
	}
}

*/