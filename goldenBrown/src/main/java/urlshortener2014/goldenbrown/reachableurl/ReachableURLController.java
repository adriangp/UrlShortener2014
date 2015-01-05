package urlshortener2014.goldenbrown.reachableurl;

import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReachableURLController {
	
	static final int TIME_OUT = 5000;
	static final boolean DEBUG = true;
	
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
//		    if (DEBUG) { System.out.println("timeout: "+huc.getConnectTimeout()); }
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
		} catch(SocketTimeoutException e){
			System.err.println(e.getMessage());
			return new ResponseEntity<>(HttpStatus.REQUEST_TIMEOUT);
		} catch (ConnectException e){
			System.err.println(e.getMessage());
			return new ResponseEntity<>(HttpStatus.REQUEST_TIMEOUT);
		} catch(IOException e){
			System.err.println(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		finally{
			if(huc != null){
				huc.disconnect();
			}
		}
	}
	
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
