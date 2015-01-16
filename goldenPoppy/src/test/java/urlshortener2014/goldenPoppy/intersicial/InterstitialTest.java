package urlshortener2014.goldenPoppy.intersicial;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.json.JsonParserFactory;

public class InterstitialTest {
	
	String target = "https://www.google.it";
	String sponsor = "http://www.unizar.es";
	String shortUrl = "";
	
	@Before
	public void interTestBefore() {
		try {
			String url = "http://localhost:8080/inter";
			HttpClient client = HttpClientBuilder.create().build();
			HttpPost post = new HttpPost(url);

			// add header
			// post.setHeader("User-Agent", USER_AGENT);

			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			urlParameters.add(new BasicNameValuePair("url", target));
			urlParameters.add(new BasicNameValuePair("sponsor", sponsor));

			post.setEntity(new UrlEncodedFormEntity(urlParameters));

			HttpResponse response = client.execute(post);

			assertEquals(201, response.getStatusLine().getStatusCode());
			
			BufferedReader rd;

			rd = new BufferedReader(new InputStreamReader(response.getEntity()
					.getContent()));

			String line = "";
			while ((line = rd.readLine()) != null) {
				if (line.startsWith("{")) {
					Map<String, Object> map = JsonParserFactory.getJsonParser().parseMap(line);
					shortUrl = (String) map.get("uri");
				}
			}

		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void interTest() {

		HttpClient client = HttpClientBuilder.create().build();
		HttpGet get = new HttpGet(shortUrl);

		HttpResponse response;
		try {
			response = client.execute(get);
			
			assertEquals(200, response.getStatusLine().getStatusCode());
			
			BufferedReader rd;

			rd = new BufferedReader(new InputStreamReader(response.getEntity()
					.getContent()));

			String line = "";
			while ((line = rd.readLine()) != null) {
				if (line.startsWith("<iframe")) {
					assertEquals(line, "<iframe src=\""+sponsor+"\" "
							+ "width=\"100%\"  height=\"100%\"></iframe>");
				}else if(line.startsWith("window.location")){
					assertEquals(line, "window.location = \"https://www.google.es\"");
					
				}
			}
			
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
