package urlshortener2014.goldenbrown.reachableurl;

import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import urlshortener2014.goldenbrown.web.UrlShortenerControllerWithLogs;

/**
* Class that provide a web service that consist of prove if an URL can be reachable using
* the HEAD petition to the URL and avoid redirections. Also the Class has a Time Out of
* 5 seconds, after that if the page isn't reachable the answer is a Not Found HTTP.
* @author: Jorge,Javi,Gabi
* @version: 08/01/2015
*/
@RestController
public class ReachableURLController {
	//Parameter that calculate the time out of the URL
	static final int TIME_OUT = 5000;
	
	private static final Logger logger = LoggerFactory.getLogger(ReachableURLController.class);
	
	/**
	* Main Method that prove is an URL (urlString) can be reachable within TIME_OUT time.
	* This methos is called by a GET petition and use the petition HEAD for prove that URL.
	* For that, we use the class HttpURLConnection that abstract the conexion with the URL
	* and provide us settings like Time out and the option of Follow Redirects.
	* @param urlString URL that we prove if can be reachable
	* @return ResponseEntity that consist of HTTP answer, OK if the URL is reachable,
	* NOT FOUND if cant be reachable or BAD REQUEST if there are any problem
	*/
	@RequestMapping(value = "/reachableurl", method = RequestMethod.GET)
	public ResponseEntity<?> isUrlReachable(@RequestParam("url") String urlString){
		URL url = null;
		HttpURLConnection huc = null;
		int code = -1;
		try{
			url = new URL(urlString);
			huc =  (HttpURLConnection)  url.openConnection(); 
		    huc.setRequestMethod("HEAD"); 
		    huc.setInstanceFollowRedirects(false);
		    huc.setConnectTimeout(TIME_OUT);
		    huc.connect(); 
		    code = huc.getResponseCode();
		    if (code == HttpURLConnection.HTTP_OK){
		    	logger.info("\""+urlString + "\" is Reachable.");
		    	return new ResponseEntity<>(HttpStatus.OK);
		    }
		    else if (code == HttpURLConnection.HTTP_MOVED_PERM || code == HttpURLConnection.HTTP_MOVED_TEMP
		    		|| code == HttpURLConnection.HTTP_SEE_OTHER){
		    	logger.info("Url has redirects, \""+urlString + "\" is Not Reachable.");
		    	return new ResponseEntity<>(HttpStatus.TEMPORARY_REDIRECT);
		    }
		    else{
		    	logger.info("\""+urlString + "\" is Not Reachable.");
		    	return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		    }
		}
		catch(IllegalArgumentException e){
			logger.error("Bad Request, cannot check if \""+urlString + "\" is reachable.");
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} catch (MalformedURLException e) {
			logger.error("Url is Malformed, cannot check if \""+urlString + "\" is reachable.");
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} catch(SocketTimeoutException e){
			logger.error("Connection Timeout, \""+urlString + "\" is Not Reachable");
			return new ResponseEntity<>(HttpStatus.REQUEST_TIMEOUT);
		} catch (ConnectException e){
			logger.error("Connection Timeout, \""+urlString + "\" is Not Reachable");
			return new ResponseEntity<>(HttpStatus.REQUEST_TIMEOUT);
		} catch(IOException e){
			logger.error("Bad Request, cannot check if \""+urlString + "\" is reachable.");
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		finally{
			if(huc != null){
				huc.disconnect();
			}
		}
	}
}
