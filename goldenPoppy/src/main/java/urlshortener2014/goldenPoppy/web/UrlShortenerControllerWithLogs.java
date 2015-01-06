package urlshortener2014.goldenPoppy.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import urlshortener2014.common.domain.ShortURL;
import urlshortener2014.common.repository.ShortURLRepository;
import urlshortener2014.common.web.UrlShortenerController;
import urlshortener2014.goldenPoppy.intesicial.IntersicialEndPoint;
import urlshortener2014.goldenPoppy.isAlive.CompruebaUrl;
import urlshortener2014.goldenPoppy.isAlive.Response;
import urlshortener2014.goldenPoppy.isAlive.URL;
import urlshortener2014.goldenPoppy.massiveLoad.Content;
import urlshortener2014.goldenPoppy.massiveLoad.Load;
import urlshortener2014.goldenPoppy.massiveLoad.Status;

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
	
	/**
	 * Método que se encarga de comprobar que la URL "url" recibida desde el cliente está viva
	 * (devuelve un 200) durante un timeout especificado. Si la URL responde antes de agotarse
	 * el timeout, devuelve un Response que encapsula 1 ó -1 dependiendo si está viva o no.
	 * Si el timeout acaba, devuelve un 0 como Response, que indica que se agotó el timeout.
	 * 
	 * @param url Con la url y el Timeout
	 * @return Response con 1, -1 ó 0
	 */
	@MessageMapping("/isalive")
    @SendToUser("/topic/isalive")
    public Response isalive(URL url) {
		
		if (!url.isValid()){
			logger.info("isAlive: Url is not valid "+url.getUrl());
			return new Response(-1);
		}
		
		logger.info("isAlive: Url is valid "+url.getUrl());
		ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Integer> future = executor.submit(new CompruebaUrl(url));
        
        int timeout = url.getTimeout();
                
        try {
        	int s = future.get(timeout, TimeUnit.SECONDS);
        	executor.shutdownNow();
            return new Response(s);
        } catch (TimeoutException e ) {
        	executor.shutdownNow();
        	return new Response(0);
        } catch (Exception e){
        	executor.shutdownNow();
        	return new Response(-1);
        }
    }
	
	//@MessageMapping("/massiveload")
	//@SendTo("/topic/massiveload")
	@RequestMapping(value = "/massiveload", method = RequestMethod.POST)
	public Status massiveLoad(@RequestParam("file") MultipartFile file,
					HttpServletRequest request){
		ArrayList<String> shorts = null;
		ArrayList<Content> longs = null;
		try {
			ThreadPoolExecutorFactoryBean factory = new ThreadPoolExecutorFactoryBean();
			InputStream input = file.getInputStream();
			BufferedReader buffer = new BufferedReader(new InputStreamReader(input));
			
			shorts = new ArrayList<String>();
			longs = new ArrayList<Content>();
			int i = 0;
			String line = buffer.readLine();
			String url = "";
			String sponsor = "";
			while (line != null){
				i++;
				url = line.split(",")[0].trim();
				try{
					sponsor = line.split(",")[1].trim();
				} catch (IndexOutOfBoundsException e1){
					sponsor = null;
				}
				longs.add(new Content(i, url, sponsor));
				if (i % 10 == 0){
					Thread t = factory.createThread(new Load(longs, shorts, this, request));
					t.run();
					longs = new ArrayList<Content>();
				}

				line = buffer.readLine();
			}
			Thread t = factory.createThread(new Load(longs, shorts, this, request));
			t.run();
		}catch (IOException e){
			
		}
		
		for (String s : shorts){
			logger.info(s);
		}
		
		return new Status(15.0, "Works");
	}
}