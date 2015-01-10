package urlshortener2014.mediumcandy.web;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import urlshortener2014.common.domain.ShortURL;
/**
 * 
 * Class in charge of controling the upload of a CSV file with a list of urls, and treat this file
 *  in order to generate a new CSV file by creating shortened urls fo the original urls.
 * 
 * @author MediumCandy
 *
 */
@Controller
public class FileUploadController {

	/**
	 * Reads each line of the CSV file stored on [filyBytes] and save each line as a URI string.
	 * 
	 * After that, creates the shortened URIs and adds them to the response forming a new
	 * CSV file for downloading on the client side.
	 * 
	 * @param fileName
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/files/{file_name}", method = RequestMethod.GET)
	public void getFile(@PathVariable("file_name") String fileNameServer,
						HttpServletResponse response) throws IOException {
		ShortURL su = null;
		File csvFile = new File("csv/"+fileNameServer+".csv");
		
		try(BufferedInputStream bis = new BufferedInputStream(
				new FileInputStream(
						new File(csvFile.getAbsolutePath())));) {
			
			OutputStream fileOut = response.getOutputStream();
			
			String uri = "", restURI="";
    		ArrayList<String> listURIs = new ArrayList<String>();
    		
    		int _char = 0;
    		
    		//Read each line of the CSV file and parses it to a String
    		while((_char = bis.read())!=-1){
    			if (_char == 13){
    				System.out.println(uri);
    				if (uri.startsWith("\"") && uri.endsWith("\"")){
    					//CSV file with quoted uris
    					uri = uri.substring(1, uri.length()-1);
    				}
    				if (!(listURIs.contains(uri))){
    					listURIs.add(uri);
    					bis.read();
    					uri = "";
    				}
				} else {
					uri += Character.toString ((char) _char);
				}
    		}
    		if (uri.length()>0){
    			//CSV file with no \r\n EOF --> add last uri
    			if (!(listURIs.contains(uri))){
					listURIs.add(uri);
					bis.read();
				}
    		}
			
    		int validURL = 0;
    		//Short the URIs from the uploaded CSV file
			for (String s : listURIs){
				restURI = linkTo(methodOn(UrlShortenerControllerWithLogs.class).
		                shortenerIfReachable(s, null, null, null)).toString();
				RestTemplate restTemplate = new RestTemplate();
				su = restTemplate.postForObject(restURI, null, ShortURL.class);
				
				//If the URI is shortened succesfully --> add to the response
				try{
					if (su.getUri() != null){
						String result = "\"" + s + "\",\"" +su.getUri().toString()+"\"\r\n"; 
						fileOut.write(result.getBytes());
					}
					validURL++;
				} catch (NullPointerException ne){
					//Invalid URI
				}
			}
			
			if (validURL == 0){
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
					"All uris the CSV file contains are either invalid or unreachable");
			} else {			
				// Setting up headers
				response.setContentType("application/x-download");
				response.setHeader("Content-disposition", "attachment; filename=" + fileNameServer + ".csv");
				response.flushBuffer();
			}
			
		} catch (NullPointerException ne){
			ne.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
					"Either your CSV has a invalid format or contains an invalid/unreachable url");
		} catch (IOException ex) {
			throw new RuntimeException("IOError writing file to output stream");
		} finally {
			csvFile.delete();
		}

	}
	
	
	/*  Generates the name for the file to download */
	private String generateString(String name) {
		String timest = new Timestamp(System.currentTimeMillis()).toString();
		String concat = name + timest;
		String hash = String.valueOf(Math.abs(concat.hashCode()));
		
		return hash;
	}
    
	
	/**
	 * Gets the uploaded CSV file from the client and transform it in an array of bytes
	 * 
	 * This is the way to handle de upload of the CSV file.
	 * 
	 * @param request
	 * @return
	 * @throws InterruptedException 
	 */
    @RequestMapping(value="/upload", method=RequestMethod.POST)
    public ResponseEntity<String> handleFileUpload(MultipartHttpServletRequest request) throws InterruptedException{
        Iterator<String> iterator = request.getFileNames();
        
        if ( iterator.hasNext() ) {
            String fileName = iterator.next();
            MultipartFile multipartFile = request.getFile(fileName);
            
            if ( !multipartFile.isEmpty() ) {
            	try {
            		String fileNameServer = "medcandy-" + generateString(fileName);
    				byte[] file = multipartFile.getBytes();
    				
    				BufferedOutputStream stream =
                            new BufferedOutputStream(new FileOutputStream(new File("csv/"+fileNameServer + ".csv" )));
                    stream.write(file);
                    stream.close();
                    
                    return new ResponseEntity<>(fileNameServer, new HttpHeaders(), HttpStatus.OK);
    			} catch (IOException e) {
    				return new ResponseEntity<>("File is empty", new HttpHeaders(), HttpStatus.BAD_REQUEST);
    			}
            } else {
            	return new ResponseEntity<>("Server Internal Error", new HttpHeaders(), HttpStatus.BAD_REQUEST);
            }
            
        }
        
        return new ResponseEntity<>("Empty Request", new HttpHeaders(), HttpStatus.BAD_REQUEST); 
    }

}