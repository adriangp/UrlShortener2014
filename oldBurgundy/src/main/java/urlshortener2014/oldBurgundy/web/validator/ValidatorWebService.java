package urlshortener2014.oldBurgundy.web.validator;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.validator.routines.UrlValidator;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ValidatorWebService {

	@RequestMapping(value = "/validator", method = RequestMethod.POST)
	public ResponseEntity<String> validateUrl(@RequestParam("url") String urlParam)  {
		HttpClient httpClient = null;  				// Objeto a traves del cual realizamos las peticiones
		HttpMethodBase request = null;				// Objeto para realizar las peticiones HTTP GET o POST
		int status = 0;         					// Codigo de la respuesta HTTP
		
		// Se instancia el objeto
		httpClient = new HttpClient();
		
		
		UrlValidator urlValidator = new UrlValidator(new String[] { "http", "https" });
		if(urlValidator.isValid(urlParam)){
			request = new GetMethod(urlParam);
		}
		else{
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		request.setFollowRedirects(false);
		
		// Se indica que reintente en caso de que haya errores.
		request.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(1, true));
		
		// Se lee el codigo de la respuesta HTTP que devuelve el servidor
		try {
			status = httpClient.executeMethod(request);
		} catch (HttpException e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);			
		} catch (UnknownHostException e){
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);	
		} catch (ConnectException e){
			return new ResponseEntity<>(HttpStatus.GATEWAY_TIMEOUT);
		} catch (IOException e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		System.out.println(status);
		
		return new ResponseEntity<>(HttpStatus.valueOf(request.getStatusCode()));
	
	}
}
