package urlshortener2014.taupegray.qr;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import urlshortener2014.taupegray.client.ByteArrayClientHttpRequest;

public class QRFetcher {

	public static ResponseEntity<?> FetchQR(URI u) {
		try {
			List<NameValuePair> qparams = new ArrayList<NameValuePair>();
			qparams.add(new BasicNameValuePair("chs", "300x300"));
			qparams.add(new BasicNameValuePair("cht", "qr"));
			qparams.add(new BasicNameValuePair("chl", u.toString()));
			@SuppressWarnings("deprecation")
			URI uri = URIUtils.createURI("http", "chart.googleapis.com", -1,
					"/chart", URLEncodedUtils.format(qparams, "UTF-8"), null);

			ByteArrayClientHttpRequest req = new ByteArrayClientHttpRequest(uri);

			return new ResponseEntity<>(req.makeGetRequest(), HttpStatus.FOUND);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
}
