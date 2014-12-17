package rest.validator;

import java.io.IOException;

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
	@Path("/uri/{url}")
	@Produces(MediaType.TEXT_PLAIN)
	public String validateUrl(@PathParam("url") String url) throws HttpException, IOException {
		HttpClient httpClient = null;  // Objeto a traves del cual realizamos las peticiones
		HttpMethodBase request = null;     // Objeto para realizar las peticiines HTTP GET o POST
		int status = 0;         // Codigo de la respuesta HTTP
		//String targetURL = "https://google.es";		
		// Instanciamos el objeto
		httpClient = new HttpClient();
		// Invocamos por Get
		request = new GetMethod(url); 
		// Indicamos reintente 3 veces en caso de que haya errores.
		request.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
		                                new DefaultHttpMethodRetryHandler(3, true));
		// Leemos el codigo de la respuesta HTTP que nos devuelve el servidor
		status = httpClient.executeMethod(request);
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
