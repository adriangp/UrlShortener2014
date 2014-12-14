package urlshortener2014.taupegray.client;

import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

public class StringClientHttpRequest extends ClientHttpRequest<String> {

	public StringClientHttpRequest(URI url) {
		super(url);
	} 
	
	public String makeGetRequest() {
		return super.makeGetRequest((Converter<String>)(HttpEntity entity) -> {try {
			return EntityUtils.toString(entity);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}});
	}
}
