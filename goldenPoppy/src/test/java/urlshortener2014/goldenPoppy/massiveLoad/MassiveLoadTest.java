package urlshortener2014.goldenPoppy.massiveLoad;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class MassiveLoadTest {

	@Rule
	public TemporaryFolder temp = new TemporaryFolder();
	
	private File testFile;
	
	@Before
	public void init() throws IOException{
		testFile = temp.newFile("test.csv");
		PrintWriter writer = new PrintWriter(testFile);
		writer.println("http://www.facebook.com, http://www.unizar.es");
		writer.println("http://www.noexisteestapagina.com");
		writer.println("http://www.unizar.es");
		writer.close();
	}
	
	@Test
	public void massiveLoadTest(){
		// Create a map with the test file
		MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
		map.add("file", new FileSystemResource(testFile.getAbsolutePath()));
		
		// Create the http request 
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		HttpEntity<MultiValueMap<String, Object>> entity = 
				new HttpEntity<MultiValueMap<String, Object>>(map, headers);
		
		RestTemplate template = new RestTemplate();
		String url = "http://localhost:8080/massiveload";
		
		// Execute the request
		ResponseEntity<Status> resp = template.exchange(url, HttpMethod.POST, entity, Status.class);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		
		// Compare response
		Status status = resp.getBody();
		assertEquals(status.getPercent(),100);
		assertEquals(status.getStatus(), "Finished");
	}
}