package urlshortener2014.oldBurgundy.web.rest.validator;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import urlshortener2014.common.domain.ShortURL;

@RestController
public class ValidatorWebService {

	private static final Logger logger = LoggerFactory.getLogger(ValidatorWebService.class);

	@RequestMapping(value = "/validator", method = RequestMethod.POST, consumes = "application/json")
	public ResponseEntity<?> validateUrl(@RequestBody Url urlParam)  {
		
		logger.info("Client solicitation url " + urlParam.getUrl());
		
		int status = httpRequest(urlParam.getUrl());
		
		return new ResponseEntity<>(HttpStatus.valueOf(status));
	}

	@RequestMapping(value = "/validator/{id}", method = RequestMethod.POST)
	public ResponseEntity<?> validateUrlInternal(@PathVariable int id, @RequestBody Url url)  {
		
		logger.info("Server solicitation id: " + id + " - url " + url.getUrl() + " - sponsor " + url.getSponsor()
				);
		
		new Thread(new HttpRequestThread(id, url.getUrl(), url.getSponsor())).start();
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	private int httpRequest(String url){
		HttpClient httpClient = null;  				// Objeto a traves del cual realizamos las peticiones
		HttpMethodBase request = null;				// Objeto para realizar las peticiones HTTP GET o POST
		
		// Se instancia el objeto
		httpClient = new HttpClient();
		
		
		UrlValidator urlValidator = new UrlValidator(new String[] { "http", "https" });
		if(urlValidator.isValid(url)){
			request = new GetMethod(url);
		}
		else{
			return HttpStatus.BAD_REQUEST.value();
		}

		request.setFollowRedirects(false);
		
		// Se indica que reintente en caso de que haya errores.
		request.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, true));
		
		// Se lee el codigo de la respuesta HTTP que devuelve el servidor
		try {
			httpClient.executeMethod(request);
		} catch (HttpException e) {
			return HttpStatus.INTERNAL_SERVER_ERROR.value();			
		} catch (UnknownHostException e){
			return HttpStatus.BAD_REQUEST.value();	
		} catch (ConnectException e){
			return HttpStatus.GATEWAY_TIMEOUT.value();
		} catch (IOException e) {
			return HttpStatus.INTERNAL_SERVER_ERROR.value();
		}
		
		return request.getStatusCode();
	}

	private class HttpRequestThread implements Runnable {
		
		private int id;
		private String url, sponsor;
		
		public HttpRequestThread(int id, String url, String sponsor){
			this.id = id;
			this.url = url;
			this.sponsor = sponsor;
		}

		@Override
		public void run() {
			int status = ValidatorWebService.this.httpRequest(url);
			
			switch(status){
				case 200:
					System.out.println("valida url");
					if(sponsor == null){
						(new RestTemplate()).postForEntity("http://localhost:8080/link/" + this.id + "?url=" + this.url, null, ShortURL.class);
						break;
					}
					else{
						status = ValidatorWebService.this.httpRequest(sponsor);
						if(status == 200){
							System.out.println("valida sponsor");
							(new RestTemplate()).postForEntity("http://localhost:8080/link/" + this.id + "?url=" + this.url + "&sponsor=" + this.sponsor, null, ShortURL.class);
							break;
						}
					}
				default:
					(new RestTemplate()).postForEntity("http://localhost:8080/csv/rest/" + this.id, status, null);
			}
		}
		
	}
}
