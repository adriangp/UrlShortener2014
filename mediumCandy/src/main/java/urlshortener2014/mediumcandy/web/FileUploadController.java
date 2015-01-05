package urlshortener2014.mediumcandy.web;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.google.common.io.ByteStreams;

@Controller
public class FileUploadController {

	@RequestMapping(value = "/files/{file_name}", method = RequestMethod.GET)
	public void getFile(@PathVariable("file_name") String fileName,
						HttpServletResponse response) {
		System.out.println("FILE NAME ====>>>    " + fileName);
		try {
			// get your file as InputStream
			FileInputStream fis = new FileInputStream( fileName + ".csv" );
			InputStream is = fis;
			// copy it to response's OutputStream
			ByteStreams.copy(is, response.getOutputStream());
			response.setContentType("text/csv");
			response.flushBuffer();
		} catch (IOException ex) {
			throw new RuntimeException("IOError writing file to output stream");
		}

	}
    
    @RequestMapping(value="/upload", method=RequestMethod.POST)
    public ResponseEntity<String> handleFileUpload(MultipartHttpServletRequest request){
        Iterator<String> iterator = request.getFileNames();
        if ( iterator.hasNext() ) {
            String fileName = iterator.next();
            MultipartFile multipartFile = request.getFile(fileName);
            
            if ( !multipartFile.isEmpty() ) {
            	try {
    				byte[] file = multipartFile.getBytes();
    				BufferedOutputStream stream =
                            new BufferedOutputStream(new FileOutputStream(new File("name.csv")));
                    stream.write(file);
                    stream.close();
                    
                    return new ResponseEntity<>("FILENAME.CSV", new HttpHeaders(), HttpStatus.OK);
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