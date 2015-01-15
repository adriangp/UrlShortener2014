package urlshortener2014.goldenPoppy.qr;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class QRTest {

	@Test
	public void qrTest(){		
		Map<String, String> req = new HashMap<String, String>();
		req.put("url", "http://www.facebook.com/");
		
		ResponseEntity<byte[]> resp = new TestRestTemplate().postForEntity(
				"http://localhost:8080/generateqr?url={url}", null, byte[].class, req);
		
		assertEquals(HttpStatus.OK, resp.getStatusCode());
	}
	
}
