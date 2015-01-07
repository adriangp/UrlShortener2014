package urlshortener2014.mediumcandy.web;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.io.BufferedOutputStream;
import java.io.File;
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

@Controller
public class FileUploadController {
	
	private byte[] fileBytes = null;

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
	public void getFile(@PathVariable("file_name") String fileName,
						HttpServletResponse response) throws IOException {
		ShortURL su = null;
		
		try {
			OutputStream fileOut = response.getOutputStream();
			
			String uri = "", restURI="";
    		ArrayList<String> listURIs = new ArrayList<String>();
    		
    		//Read each line of the uploaded file and save each line as a URI string
    		for (int i=0; i<fileBytes.length; i++){
				if (fileBytes[i] == 13){
					listURIs.add(uri);
					i++;
					uri = "";
				} else {
					uri += Character.toString ((char) fileBytes[i]);
				}
			}
			
    		//Short the URIs from the uploaded CSV file
			for (String s : listURIs){
				restURI = linkTo(methodOn(UrlShortenerControllerWithLogs.class).
		                shortenerIfReachable(s, null, null, null)).toString();
				RestTemplate restTemplate = new RestTemplate();
				su = restTemplate.postForObject(restURI, null, ShortURL.class);
				
				//If the URI is shortened succesfully --> add to the response
				if (su.getUri() != null){
					String result = "\"" + s + "\",\"" +su.getUri().toString()+"\"\r\n"; 
					fileOut.write(result.getBytes());
				}
			}
			// Deleting File
			File file = new File( fileName + ".csv" );
			file.delete();
			// Setting up headers
			response.setContentType("application/x-download");
			response.setHeader("Content-disposition", "attachment; filename=" + fileName + ".csv");
			response.flushBuffer();
			
		} catch (NullPointerException ne){
			// Deleting File
			File file = new File( fileName + ".csv" );
			file.delete();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
					"Either your CSV has a invalid format or contains an invalid/unreachable url");
		} catch (IOException ex) {
			throw new RuntimeException("IOError writing file to output stream");
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
	 */
    @RequestMapping(value="/upload", method=RequestMethod.POST)
    public ResponseEntity<String> handleFileUpload(MultipartHttpServletRequest request){
        Iterator<String> iterator = request.getFileNames();
        
        if ( iterator.hasNext() ) {
            String fileName = iterator.next();
            MultipartFile multipartFile = request.getFile(fileName);
            
            if ( !multipartFile.isEmpty() ) {
            	try {
            		String fileNameServer = "medcandy-" + generateString(fileName);
    				byte[] file = multipartFile.getBytes();
    				fileBytes = file;
    				
    				BufferedOutputStream stream =
                            new BufferedOutputStream(new FileOutputStream(new File( fileNameServer + ".csv" )));
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