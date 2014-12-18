package urlshortener2014.goldenbrown.reachableurl;

import java.io.IOException;
import java.net.UnknownHostException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReachableURL {
	
	//http://hc.apache.org/httpclient-3.x/tutorial.html
	@RequestMapping(value = "/reachableurl", method = RequestMethod.POST)
	public static String isUrlReachable(@RequestParam("url") String url){
		String ok = "OK", error = "ERROR";
//		URL = parseURL(URL);
	    // Create an instance of HttpClient.
	    HttpClient client = new HttpClient();
	    
	    // Create a method instance.
	    HeadMethod method = new HeadMethod(url);

	    // Provide custom retry handler is necessary
	    method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
	    		new DefaultHttpMethodRetryHandler(3, false));

	    try {
	      // Execute the method.
	      int statusCode = client.executeMethod(method);

	      if (statusCode != HttpStatus.SC_OK) {
	        System.err.println("Method failed: " + method.getStatusLine());
	        return error;
	      }

	      // Read the response body.
	      byte[] responseBody = method.getResponseBody();

	      // Deal with the response.
	      // Use caution: ensure correct character encoding and is not binary data
	      //System.out.println(new String(responseBody));
	      return ok;

		} catch (HttpException e) {
			System.err.println("Fatal protocol violation: " + e.getMessage());
			e.printStackTrace();
			return error;
		} catch (UnknownHostException e) {
			System.err.println("UnknownHostException: " + e.getMessage());
			//e.printStackTrace();
			return error;
		} catch (IOException e) {
			System.err.println("IOException: " + e.getMessage());
			e.printStackTrace();
			return error;
		} catch (Exception e) {
			System.err.println("Exception: " + e.getMessage());
			e.printStackTrace();
			return error;
		} finally {
			// Release the connection.
			method.releaseConnection();
		}
	}

//	private static String parseURL(String url) {
//		if (!url.startsWith("http://") && !url.startsWith("https://")){
//			url = "http://"+url;
//		}
//		return url;
//	}

}
