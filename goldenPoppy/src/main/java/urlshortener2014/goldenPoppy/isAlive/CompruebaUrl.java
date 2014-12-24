package urlshortener2014.goldenPoppy.isAlive;

import java.util.concurrent.Callable;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

public class CompruebaUrl implements Callable<String>{
	
	private URL url;
	public CompruebaUrl(URL url){
		this.url = url;
	}
	
	@Override
    public String call() throws Exception {
		HttpClient client = HttpClientBuilder.create().build();
    	//HttpHead request = new HttpHead(url.getUrl());
    	HttpGet request = new HttpGet(url.getUrl());

    	HttpResponse response = client.execute(request);
    	
    	int resultado = response.getStatusLine().getStatusCode();
    	
    	if (resultado == 200){
    		return "La Url "+url.getUrl()+" está viva";
    	}else if (resultado == 501){
    		return "Hubo un problema al comprobar la URL";
    	}else{
    		return "La Url "+url.getUrl()+" NO está viva";
    	}
        
    }

}
