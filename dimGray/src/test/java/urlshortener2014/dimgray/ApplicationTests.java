package urlshortener2014.dimgray;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tomcat.util.http.fileupload.disk.DiskFileItem;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import urlshortener2014.common.domain.ShortURL;
import urlshortener2014.common.repository.fixture.ClickFixture;
import urlshortener2014.dimgray.domain.InfoDBList;
import urlshortener2014.dimgray.domain.UrlPair;
import urlshortener2014.dimgray.domain.UrlPairs;
import urlshortener2014.dimgray.web.fixture.ShortURLFixture;
import urlshortener2014.dimgray.Application;
import urlshortener2014.dimgray.web.UrlShortenerControllerWithLogs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port=0")
@DirtiesContext
public class ApplicationTests {

	@Value("${local.server.port}")
	private int port = 0;
	
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	private File csvFile;
	
	@Before
	public void setup() throws IOException {
		csvFile = folder.newFile("testFile.csv");
		BufferedWriter out = new BufferedWriter(new FileWriter(csvFile));
		out.write("http://www.unizar.es\n");
		out.write("www.unizar.es\n");
		out.close();
	}

	@Test
	public void testHome() throws Exception {
		ResponseEntity<String> entity = new TestRestTemplate().getForEntity(
				"http://localhost:" + this.port, String.class);
		assertEquals(HttpStatus.OK, entity.getStatusCode());
		assertTrue("Wrong body (title doesn't match):\n" + entity.getBody(), entity
				.getBody().contains("<title>URL"));
	}

	@Test
	public void testCss() throws Exception {
		ResponseEntity<String> entity = new TestRestTemplate().getForEntity(
				"http://localhost:" + this.port
						+ "/webjars/bootstrap/3.0.3/css/bootstrap.min.css", String.class);
		assertEquals(HttpStatus.OK, entity.getStatusCode());
		assertTrue("Wrong body:\n" + entity.getBody(), entity.getBody().contains("body"));
		assertEquals("Wrong content type:\n" + entity.getHeaders().getContentType(),
				MediaType.valueOf("text/css;charset=UTF-8"), entity.getHeaders().getContentType());
	}
	
	@Test
	public void testQRService() throws Exception {
		ShortURL su = ShortURLFixture.someUrl();
		Map<String,String> aux = new HashMap<String,String>();
		aux.put("url","http://localhost:" + this.port+"/l"+ ClickFixture.click(su).getHash());
		ResponseEntity<byte[]> entity = new TestRestTemplate().getForEntity(
				"http://localhost:" + this.port+"/qr?url={url}", byte[].class,aux);
		assertEquals(HttpStatus.CREATED, entity.getStatusCode());
	}
	
	@Test
	public void testCSVService() throws Exception {
		RestTemplate restTemplate = new RestTemplate();
         String uri = "http://localhost:" + this.port+"/upload";
         MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
         map.add("file", new FileSystemResource(csvFile.getPath()));
         HttpHeaders imageHeaders = new HttpHeaders();
         imageHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
         HttpEntity<MultiValueMap<String, Object>> imageEntity = new HttpEntity<MultiValueMap<String, Object>>(map, imageHeaders);
         ResponseEntity<UrlPairs>  entity = restTemplate.exchange(uri, HttpMethod.POST, imageEntity, UrlPairs.class);
         assertEquals(HttpStatus.OK, entity.getStatusCode());
         UrlPairs up = entity.getBody();
         assertNotSame(up.getUrlPairs().get(0).getShortenedUrl(),null); //la primera url es correcta.
         assertEquals(up.getUrlPairs().get(1).getShortenedUrl(),null); //la segunda url es incorrecta.
         
	}
	
	@Test
	public void testDBService() throws Exception {
		ResponseEntity<InfoDBList> entity = new TestRestTemplate().getForEntity(
				"http://localhost:" + this.port+"/showInfo", InfoDBList.class);
		assertEquals(HttpStatus.OK, entity.getStatusCode());
	}

}