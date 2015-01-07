package urlshortener2014.taupegray.safebrowsing;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import urlshortener2014.taupegray.client.StringClientHttpRequest;

public class SafeBrowsing {
	/**
	 * Tests if an URL is saf
	 * @param url URL to test
	 * @return whether it's safe or not
	 */
	public static boolean isSafe(String url) {
		try {
			List<NameValuePair> qparams = new ArrayList<NameValuePair>();
			qparams.add(new BasicNameValuePair("client", "URLShortener2014"));
			qparams.add(new BasicNameValuePair("key", "AIzaSyBYxMvX8mbdvuCyE2oLrb_EENCXcFg0JdI"));
			qparams.add(new BasicNameValuePair("appver", "1.5.2"));
			qparams.add(new BasicNameValuePair("pver", "3.1"));
			qparams.add(new BasicNameValuePair("url", url));
			@SuppressWarnings("deprecation")
			URI uri = URIUtils.createURI("https", "sb-ssl.google.com", -1,
					"/safebrowsing/api/lookup", URLEncodedUtils.format(qparams, "UTF-8"), null);

			StringClientHttpRequest req = new StringClientHttpRequest(uri);
			String response = req.makeGetRequest();
			
			if(response == null) {
				return true;
			}
			else {
				return false;
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return false;
		}
	}
}
