package urlshortener2014.taupegray.client;

import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

public class ByteArrayClientHttpRequest extends ClientHttpRequest<byte[]> {

	public ByteArrayClientHttpRequest(URI url) {
		super(url);
	} 
	
	public byte[] makeGetRequest() {
		return super.makeGetRequest((Converter<byte[]>)(HttpEntity entity) -> {try {
			return EntityUtils.toByteArray(entity);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}});
	}
}
