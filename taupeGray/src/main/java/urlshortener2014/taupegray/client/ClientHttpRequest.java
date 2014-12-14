package urlshortener2014.taupegray.client;

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class ClientHttpRequest<Type> {
	
	protected interface Converter<T> {
	    T converFrom(HttpEntity entity);
	}
	
	protected URI url;
	
	public ClientHttpRequest(URI url) {
		this.url = url;
	}
	
	public Type makeGetRequest(Converter<Type> converter) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		Type responseBody = null;
		try {
            HttpGet httpget = new HttpGet(url);

            System.out.println("Executing request " + httpget.getRequestLine());

            ResponseHandler<Type> responseHandler = new ResponseHandler<Type>() {

                public Type handleResponse(
                        final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? converter.converFrom(entity) : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }

            };
            responseBody = httpclient.execute(httpget, responseHandler);
        } catch (IOException e) {
			e.printStackTrace();
		} finally {
            try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
		return responseBody;
	}
}