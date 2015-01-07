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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import urlshortener2014.common.domain.ShortURL;
import urlshortener2014.oldBurgundy.web.errorcontroler.ErrorMvcAutoConfiguration;

/**
 * Rest controller of the validator web service
 */
@RestController
public class ValidatorWebService {
	
	@Autowired
	String hostCore;
	
	@Autowired
	String hostCsv;

	private static final Logger logger = LoggerFactory.getLogger(ValidatorWebService.class);

	/**
	 * Validate URL
	 * @param urlParam URL to validate
	 * @return
	 */
	@RequestMapping(value = "/validator", method = RequestMethod.POST, consumes = "application/json")
	public ResponseEntity<?> validateUrl(@RequestBody Url urlParam)  {
		
		logger.info("client request the url validation " + urlParam.getUrl());
		
		int status = httpRequest(urlParam.getUrl());
		
		return ErrorMvcAutoConfiguration.responseError(HttpStatus.valueOf(status));
	}

	/**
	 * 
	 * Validate URL
	 * @param id The <i>id</i> of the work
	 * @param url URL to validate
	 * @return
	 */
	@RequestMapping(value = "/validator/{id}", method = RequestMethod.POST)
	public ResponseEntity<?> validateUrlInternal(@PathVariable int id, @RequestBody Url url)  {
		
		logger.info("Server solicitation id: " + id + " - url " + url.getUrl() + " - sponsor " + url.getSponsor());
		
		new Thread(new HttpRequestThread(id, url.getUrl(), url.getSponsor())).start();
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/**
	 * Check the URL
	 * @param url URL to validate
	 * @return The status code of the response
	 */
	private int httpRequest(String url){
		HttpClient httpClient = new HttpClient();;
		HttpMethodBase request = null;		
		
		UrlValidator urlValidator = new UrlValidator(new String[] { "http", "https" });
		if(urlValidator.isValid(url)){
			request = new GetMethod(url);
		}
		else{
			return HttpStatus.BAD_REQUEST.value();
		}

		request.setFollowRedirects(false);
		
		request.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, true));
		
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

	/**
	 * Thread to validate and send the request short URL
	 */
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
					if(sponsor == null){
						(new RestTemplate()).postForEntity(hostCore + "/link/" + this.id + "?url=" + this.url, null, ShortURL.class);
						break;
					}
					else{
						status = ValidatorWebService.this.httpRequest(sponsor);
						if(status == 200){
							(new RestTemplate()).postForEntity(hostCore + "/link/" + this.id + "?url=" + this.url + "&sponsor=" + this.sponsor, null, ShortURL.class);
							break;
						}
					}
				default:
					(new RestTemplate()).postForEntity(hostCsv + "/csv/rest/" + this.id, status, null);
			}
		}
		
	}
}
