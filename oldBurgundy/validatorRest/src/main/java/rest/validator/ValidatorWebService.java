package rest.validator;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

@Path("/validator")
public class ValidatorWebService {
	
	@GET
	@Path("/uri/{url}/{protocolo}")
	@Produces(MediaType.TEXT_PLAIN)
	public String validateUrl(@PathParam("url") String url,
			@PathParam("protocolo") int protocolo)  {
		HttpClient httpClient = null;  // Objeto a traves del cual realizamos las peticiones
		HttpMethodBase request = null;     // Objeto para realizar las peticiones HTTP GET o POST
		int status = 0;         // Codigo de la respuesta HTTP
		//String targetURL = "https://google.es";		
		// Instanciamos el objeto
		httpClient = new HttpClient();
		
		// Invocamos por Get
		if (protocolo==1)
			request = new GetMethod("https://" + url); 
		else if (protocolo==2)
			request = new GetMethod("http://" + url); 
		else
			return "Error: protocol not suported";

		request.setFollowRedirects(false);
		
		// Indicamos reintente en caso de que haya errores.
		request.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
		                                new DefaultHttpMethodRetryHandler(1, true));
		
		// Leemos el codigo de la respuesta HTTP que nos devuelve el servidor
		try {
			status = httpClient.executeMethod(request);
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} catch (UnknownHostException e){
			e.printStackTrace();
			return "Error: url bad formed";
		} catch (ConnectException e){
			e.printStackTrace();
			return "Error: Acces https not posible";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Vemos si la peticion se ha realizado satisfactoriamente
		if (status != HttpStatus.SC_OK) {
			String error = "Error\t" + request.getStatusCode() + "\t" + 
                    request.getStatusText() + "\t" + request.getStatusLine();
			System.out.println(error);	        	 
			return error;
		}
		return "SC_OK";
	
	}
	
	@POST
	@Path("/uri")
	@Consumes(MediaType.APPLICATION_JSON)
	public String validateUrlPOst(Uri url)  {
		HttpClient httpClient = null;  // Objeto a traves del cual realizamos las peticiones
		HttpMethodBase request = null;     // Objeto para realizar las peticiones HTTP GET o POST
		int status = 0;         // Codigo de la respuesta HTTP
		//String targetURL = "https://google.es";		
		// Instanciamos el objeto
		httpClient = new HttpClient();
		
		// Invocamos por Get
		if (url.getProtocol()==1)
			request = new GetMethod("https://" + url.getUrl()); 
		else if (url.getProtocol()==2)
			request = new GetMethod("http://" + url.getUrl()); 
		else
			return "Error: protocol not suported";

		request.setFollowRedirects(false);
		
		// Indicamos reintente en caso de que haya errores.
		request.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
		                                new DefaultHttpMethodRetryHandler(1, true));
		
		// Leemos el codigo de la respuesta HTTP que nos devuelve el servidor
		try {
			status = httpClient.executeMethod(request);
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} catch (UnknownHostException e){
			e.printStackTrace();
			return "Error: url bad formed";
		} catch (ConnectException e){
			e.printStackTrace();
			return "Error: Acces https not posible";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Vemos si la peticion se ha realizado satisfactoriamente
		if (status != HttpStatus.SC_OK) {
			String error = "Error\t" + request.getStatusCode() + "\t" + 
                    request.getStatusText() + "\t" + request.getStatusLine();
			System.out.println(error);	        	 
			return error;
		}
		return "SC_OK";
	
	}
}
