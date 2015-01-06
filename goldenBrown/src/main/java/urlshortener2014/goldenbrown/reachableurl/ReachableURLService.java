package urlshortener2014.goldenbrown.reachableurl;

import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
/**
 * Class that provide a web service that consist of prove if an URL can be reachable using
 * the HEAD petition to the URL and avoid redirections. Also the Class has a Time Out of 
 * 5 seconds, after that if the page isn't reachable the answer is a Not Found HTTP.
 * @author: Jorge,Javi,Gabi
 * @version: 08/01/2015
 */
@RestController
public class ReachableURLService {
	//Parameter that calculate the time out of the URL
	static final int TIME_OUT = 5000;
	static final boolean DEBUG = true;//TODO:ELIMINATE
	
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
//TODO:ELIMINATE  if (DEBUG) { System.out.println("timeout: "+huc.getConnectTimeout()); }
		    if (code == HttpURLConnection.HTTP_OK){
		    	return new ResponseEntity<>(HttpStatus.OK);
		    }
		    else{
		    	return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		    }
		}
		catch(IllegalArgumentException e){
			System.err.println(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} catch (MalformedURLException e) {
			System.err.println(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} catch (ConnectException e){
			System.err.println(e.getMessage());
			return new ResponseEntity<>(HttpStatus.REQUEST_TIMEOUT);
		} catch(IOException e){
			System.err.println(e.getMessage());
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		finally{
			if(huc != null){
				huc.disconnect();
			}
		}
	}
	//TODO: ELIMINATE METHOD
	private static String parseURL(String url) throws IllegalArgumentException {
		if (url != null){
			if (!url.startsWith("http://") && !url.startsWith("https://")){
				url = "http://"+url;
			}
			return url;
		}
		else{
			throw new IllegalArgumentException("URL cannot be null.");
		}
	}
}
