package urlshortener2014.goldenPoppy.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
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
	
	private Status status = new Status();
	
	/**
	 * Method that redirect to an URL. If the URL has an sponsor associated, 
	 * user will be rediret to a page with a banner and the sponsor, and past
	 * 10 seconds user will be redirect to the long URL.
	 * 
	 * @param id Short URL.
	 * @param request Http request.
	 * @return Response with an URL with the banner.
	 */
	public ResponseEntity<?> redirectTo(@PathVariable String id, 
			HttpServletRequest request) {
		logger.info("Requested redirection with hash "+id);
		if(shortURLRepository.findByKey(id).getSponsor() == null){
			return super.redirectTo(id, request);
		}else{
			return inter.redireccionarPubli(id);
		}
	}
	
	/**
	 * Method that allow to the user to short an URL.
	 * 
	 * @param url Long URL to short.
	 * @param sponsor Sponsor associated to the URL.
	 * @param brand Brand associated to the URL.
	 * @param request Http request.
	 * @return Response with the short URL.
	 */
	public ResponseEntity<ShortURL> shortener(@RequestParam("url") String url,
			@RequestParam(value = "sponsor", required = false) String sponsor,
			@RequestParam(value = "brand", required = false) String brand,
			HttpServletRequest request) {
		if(sponsor == null){
			logger.info("Requested new short for uri "+url);
		}else{
			logger.info("Requested sponsor "+sponsor+" for uri "+url);
		}
		
		return super.shortener(url, sponsor, brand, request);
	}
	
	/**
	 * Crea una nueva URL corta que apunta a la URL con el Sponsor
	 * @param sUrl
	 * @param sponsor
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/inter", method = RequestMethod.POST)
	public ResponseEntity<ShortURL> intersicial(@RequestParam("url") String sUrl,

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
	
	/**
	 * Method that allow to the user to load a file with some URLs. 
	 * 
	 * @param file File that contains the URLs.
	 * @param request Http request.
	 * @return Response
	 */
	@RequestMapping(value = "/massiveload", method = RequestMethod.POST)
	public ResponseEntity<Status> massiveLoad(@RequestParam("file") MultipartFile file,
					HttpServletRequest request){
		status = new Status();
		int filename = request.hashCode();
		String urlFile = "http://" + request.getLocalName() + ":8080/files/" + 
				filename + ".csv"; 
		
		status.setPercent(0);
		status.setStatus("Loading...");
		status.setURL(urlFile);
		
		List<Content> shorts = new ArrayList<Content>();
		List<Content> longs = null;
		List<Future<Content>> futures = new ArrayList<Future<Content>>();
		InputStream input;
		try {
			ExecutorService executor = Executors.newFixedThreadPool(100);
			
			input = file.getInputStream();
			BufferedReader buffer = new BufferedReader(new InputStreamReader(input));
			longs = new ArrayList<Content>();
			
			int i = 0;
			String url = "";
			String sponsor = "";
			String line = buffer.readLine();
			
		
			while (line != null){
				// Reading the file.
				i++;
				url = line.split(",")[0].trim();
				try{
					sponsor = line.split(",")[1].trim();
				} catch (IndexOutOfBoundsException e1){
					sponsor = null;
				}
				Content c = new Content(i, url, sponsor);
				longs.add(c);
				line = buffer.readLine();
			}
			
			int j = 0;
			for (Content c : longs){
				j++;
				status.setStatus("In Process");
				status.setPercent(100*j/longs.size());
				futures.add(executor.submit(new Load(c, this, request)));
			}
			
			for (Future<Content> f : futures){
				// Wait all the threads and take the result of the load.
				shorts.add(f.get());
				status.setPercent(100*shorts.size()/longs.size());
			}
			
			// Create a file with the result of the load.
			status.setStatus("Writing in file. Wait to the work be finished.");
			writeInFile(shorts, filename + ".csv");
			status.setStatus("Finished");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			logger.info("Interrupt Error");
		} catch (ExecutionException e) {
			logger.info("Execution Error");
		}
		return new ResponseEntity<>(status, HttpStatus.OK);
	}
	
	/**
	 * Method that inform to the user about the progress of the load.
	 * 
	 * @return Object that contains the percent, the status and the URL with the file.
	 */
	@MessageMapping("/massiveloadws")
	@SendToUser("/topic/massiveloadws")
	public Status massiveloadws(){
		return new Status(this.status.getPercent(), this.status.getStatus(),
				this.status.getUrl());
	}
	
	/**
	 * Private method that write in a file the result of the massive load.
	 * 
	 * @param shorts List with the URLs shortened.
	 * @param hash Name of the file that will contains the URL shortened.
	 */
	private void writeInFile(List<Content> shorts, String hash){
		try {
			File path = new File("tmp/files");
			if (path.mkdirs())
				logger.info("Massive Load: Folder created");
			else 
				logger.info("Massive Load: Folder already exists");
			
			File f = new File("tmp/files/"+hash);
			f.createNewFile();

			PrintWriter writer = new PrintWriter(f);
			for (Content c : shorts) {
				writer.println(c.getURL() + ", " + c.getSponsor());
			}
			writer.close();
		} catch (IOException e) {
			logger.info("Massive Load: Error writing at the file.");
		}
	}
	
	/**
	 * Method that returns a file with the URLs shortened.
	 * 
	 * @param id Name of the file.
	 * @param request Http request.
	 * @return File with thr URLs shortened.
	 */
	@RequestMapping(value = "/files/{id}", method = RequestMethod.GET)
	public ResponseEntity<byte[]> getFile(@PathVariable String id,
			HttpServletRequest request) {
		HttpHeaders h = new HttpHeaders();
		h.setContentType(MediaType.MULTIPART_FORM_DATA);
		
		File file = new File("tmp/files/"+id+".csv");
		byte[] result = null;
		try{
	    	 result = IOUtils.toByteArray(new FileInputStream(file));
	    	 FileUtils.writeByteArrayToFile(file, result);
		} catch (FileNotFoundException e) {
			logger.info("Massive Load: Error: File don't exists");
			return new ResponseEntity<>(h,HttpStatus.BAD_REQUEST);
		} catch (IOException e) {
			logger.info("Massive Load: Error: Can't Read the file");
			return new ResponseEntity<>(h,HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(result,h,HttpStatus.OK);
	}
}