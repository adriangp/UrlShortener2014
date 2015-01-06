package urlshortener2014.goldenPoppy.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
	 * 
	 * @param file
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/massiveload", method = RequestMethod.POST)
	public ResponseEntity<List<Content>> massiveLoad(@RequestParam("file") MultipartFile file,
					HttpServletRequest request){
		List<Content> shorts = null;
		List<Content> longs = null;
		List<Future<List<Content>>> futures = new ArrayList<Future<List<Content>>>();
		try {
			ExecutorService executor = Executors.newFixedThreadPool(100);
			
			InputStream input = file.getInputStream();
			BufferedReader buffer = new BufferedReader(new InputStreamReader(input));
			
			long totalSize = file.getSize();
			long nBytes = 0;
			shorts = new ArrayList<Content>();
			longs = new ArrayList<Content>();
			int i = 0;
			String line = buffer.readLine();
			String url = "";
			String sponsor = "";
			
			while (line != null){
				nBytes += line.getBytes().length;
				i++;
				url = line.split(",")[0].trim();
				try{
					sponsor = line.split(",")[1].trim();
				} catch (IndexOutOfBoundsException e1){
					sponsor = null;
				}
				longs.add(new Content(i, url, sponsor));
				if (i % 10 == 0){
					Future<List<Content>> future = executor.submit(new Load(longs, this, request));
					futures.add(future);
					massiveloadws(100*nBytes/totalSize, "In process", null);
					longs = new ArrayList<Content>();
				}

				line = buffer.readLine();
			}
			
			Future<List<Content>> future = executor.submit(new Load(longs, this, request));
			futures.add(future);
			for (Future<List<Content>> f : futures){
				List<Content> l = f.get();
				for (Content c : l)
					shorts.add(c.getId(),c);
			}
			
			writeInFile(shorts, request.hashCode() + ".csv");
			massiveloadws(100*nBytes/totalSize, "Finished", request.hashCode() + ".csv");
		}catch (IOException e){
			logger.info("IO Error reading the file " + file.getOriginalFilename());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} 
		
		return new ResponseEntity<>(shorts, HttpStatus.OK);
	}
	
	/**
	 * 
	 * @param progress
	 * @param status
	 * @param url
	 * @return
	 */
	@MessageMapping("/massiveloadws")
	@SendToUser("/topic/massiveloadws")
	public Status massiveloadws(double progress, String status, String url){
		return new Status(progress, status, url);
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
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(result,h,HttpStatus.OK);
	}
	
	/**
	 * Private method that write in a file the result of the massive load.
	 * 
	 * @param shorts List with the URLs shortened.
	 * @param hash Name of the file that will contains the URL shortened.
	 */
	private void writeInFile(List<Content> shorts, String hash){
		FileWriter fileWriter = null;
		try{
			fileWriter = new FileWriter("tmp/files/"+hash);
			for (Content c : shorts){
				fileWriter.write(c.getURL() + ", " + c.getSponsor());
			}			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != fileWriter)
					fileWriter.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

}