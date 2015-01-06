package urlshortener2014.goldenPoppy.isAlive;

import java.util.concurrent.Callable;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
/**
 * 
 * @author Javier Tello
 * 
 * Clase que implementa a Callable, cuyo método call comprueba 
 * que la URL es válida (Petición HTTP devuelve 200). En ese caso
 * devuelve 1, en caso contrario -1
 */
public class CompruebaUrl implements Callable<Integer>{
	
	// Atributo URL
	private URL url;
	
	/**
	 * Constructor de objetos CompruebaUrl
	 * @param url
	 */
	public CompruebaUrl(URL url){
		this.url = url;
	}
	
	/**
	 * Implementa el método call(), que, al ser llamado por el ExecutorService,
	 * comprueba que la URL "url" es válida. En caso de serlo devuelve 1, en caso
	 * contrario -1.
	 * @return {1 si es válida, -1 si no es válida}
	 */
	@Override
    public Integer call() throws Exception {

		HttpClient client = HttpClientBuilder.create().build();
    	//HttpHead request = new HttpHead(url.getUrl());
    	HttpGet request = new HttpGet(url.getUrl());

    	HttpResponse response = client.execute(request);
    	
    	int resultado = response.getStatusLine().getStatusCode();
    	
    	if (resultado == 200){
    		return 1;
    	}else{
    		return -1;
    	}
        
    }
	
	

}
