package urlshortener2014.dimgray.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import urlshortener2014.common.domain.ShortURL;
import urlshortener2014.common.web.UrlShortenerController;
import urlshortener2014.dimgray.concurrent.UrlShortenerTask;
import urlshortener2014.dimgray.domain.UrlPair;

@RestController
public class UrlShortenerControllerWithLogs extends UrlShortenerController {

	private static final Logger logger = LoggerFactory.getLogger(UrlShortenerControllerWithLogs.class);
	
	private static final ExecutorService ex = Executors.newCachedThreadPool();
	
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
	
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public ResponseEntity<List<UrlPair>> csvShortener(@RequestParam("file") MultipartFile file,
			@RequestParam(value = "sponsor", required = false) String sponsor,
			@RequestParam(value = "brand", required = false) String brand,
			HttpServletRequest request) {
		logger.info("Requested new file: "+file.getName());
		try (InputStream inputStream = file.getInputStream();
			 BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream));){
			List<Future<UrlPair>> futures = new ArrayList<Future<UrlPair>>();
			
			List<UrlPair> list = new ArrayList<UrlPair>();
			String line = bf.readLine();
			while(line != null){
				logger.info("Data: "+line);
				futures.add(ex.submit(new UrlShortenerTask(line.trim(),request,sponsor,brand, this)));
				line = bf.readLine();
			}
			 for(Future<UrlPair> future: futures){
				UrlPair pair = future.get();
				list.add(pair);
			}
			return new ResponseEntity<>(list,HttpStatus.OK);
		} catch (IOException | InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		
	}
}

