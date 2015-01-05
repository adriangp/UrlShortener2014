package urlshortener2014.mediumcandy.web;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Controller
public class FileUploadController {

    @RequestMapping(value="/upload", method=RequestMethod.GET)
    public @ResponseBody String provideUploadInfo() {
        return "You can upload a file by posting to this same URL.";
    }
    
    @RequestMapping(value="/upload", method=RequestMethod.POST)
    public @ResponseBody String handleFileUpload(MultipartHttpServletRequest request){
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
                    return "You successfully uploaded !";
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    				return "You failed to upload  => " + e.getMessage();
    			}
            } else {
            	return "You failed to upload => EMPTY FILE!";
            }
        }
        
        return "No files found in request!"; 
    }

}