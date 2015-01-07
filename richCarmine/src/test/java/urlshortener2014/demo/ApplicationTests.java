package urlshortener2014.demo;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import urlshortener2014.richcarmine.Application;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port=0")
@DirtiesContext
public class ApplicationTests {

	@Value("${local.server.port}")
	private int port = 0;

    @Autowired
    private WebApplicationContext ctx;

    private MockMvc mockMvc;

    /**
     * Set up a MockMVC of this application.
     */
    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx).build();
    }

	@Test
	public void testHome() throws Exception {
		ResponseEntity<String> entity = new TestRestTemplate().getForEntity(
				"http://localhost:" + this.port, String.class);
		assertEquals(HttpStatus.OK, entity.getStatusCode());
		assertTrue("Wrong body (title doesn't match):\n" + entity.getBody(), entity
				.getBody().contains("<title>PistachoShortener"));
	}

	@Test
	public void testCss() throws Exception {
		ResponseEntity<String> entity = new TestRestTemplate().getForEntity(
				"http://localhost:" + this.port
						+ "/webjars/bootstrap/3.1.1/css/bootstrap.min.css", String.class);
		assertEquals(HttpStatus.OK, entity.getStatusCode());
		assertTrue("Wrong body:\n" + entity.getBody(), entity.getBody().contains("body"));
		assertEquals("Wrong content type:\n" + entity.getHeaders().getContentType(),
				MediaType.valueOf("text/css;charset=UTF-8"), entity.getHeaders().getContentType());
	}

    /**
     * Method that creates a ShortURL via QR service and verifies its functionality.
     * @throws Exception
     */
    @Test
    public void testQRrize() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
        body.add("url","http://www.pistachosoft.com/");
        HttpEntity<?> entity = new HttpEntity<>(body,headers);
        ResponseEntity<String> qrResponse = new TestRestTemplate().exchange(
                "http://localhost:" + this.port + "/qr",
                HttpMethod.POST,
                entity,
                String.class);
        assertEquals(HttpStatus.CREATED, qrResponse.getStatusCode());
        JSONObject json = new JSONObject(qrResponse.getBody());
        assertEquals(json.get("target"),"http://www.pistachosoft.com/");
        assertEquals(json.get("hash"),"8511a654");
    }

    /**
     * Method that first creates a ShortUrl via QR service, then retrieves the QR code
     * both from this application (proxied) and from google API, comparing both images
     * to check whether they are equal.
     * @throws Exception
     */
    @Test
    public void testRedirectQR() throws Exception {
        //Create short url
        HttpHeaders qrHeaders = new HttpHeaders();
        qrHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
        body.add("url","http://www.pistachosoft.com/");
        HttpEntity<?> qrEntity = new HttpEntity<>(body,qrHeaders);
        ResponseEntity<String> qrResponse = new TestRestTemplate().exchange(
                "http://localhost:" + this.port + "/qr",
                HttpMethod.POST,
                qrEntity,
                String.class);
        assertEquals(HttpStatus.CREATED, qrResponse.getStatusCode());
        JSONObject json = new JSONObject(qrResponse.getBody());
        assertEquals(json.get("target"),"http://www.pistachosoft.com/");
        assertEquals(json.get("hash"),"8511a654");

        //Get google QR code
        HttpHeaders proxyHeaders = new HttpHeaders();
        proxyHeaders.setContentType(MediaType.IMAGE_PNG);
        HttpEntity<String> proxyEntity = new HttpEntity<>(proxyHeaders);
        String url = "https://chart.googleapis.com/chart?cht=qr&chs=300x300&chl=" +
                json.getString("uri") +
                "&choe=UTF-8";
        ResponseEntity<?> qrCodeResponse = new TestRestTemplate().exchange(
                url,
                HttpMethod.GET,
                proxyEntity,
                byte[].class);
        assertEquals(HttpStatus.OK, qrCodeResponse.getStatusCode());
        assertEquals(MediaType.IMAGE_PNG,qrCodeResponse.getHeaders().getContentType());

        //Get proxied QR code
        HttpHeaders codeHeaders = new HttpHeaders();
        codeHeaders.setContentType(MediaType.IMAGE_PNG);
        HttpEntity<String> codeEntity = new HttpEntity<>(codeHeaders);
        ResponseEntity<?> qrProxyCodeResponse = new TestRestTemplate().exchange(
                "http://localhost:" + this.port + "/qr" + json.getString("hash"),
                HttpMethod.GET,
                codeEntity,
                byte[].class);
        assertEquals(HttpStatus.CREATED, qrProxyCodeResponse.getStatusCode());
        assertEquals(MediaType.IMAGE_PNG,qrCodeResponse.getHeaders().getContentType());

        byte[] qrCode = (byte[]) qrCodeResponse.getBody();
        byte[] qrProxyCode = (byte[]) qrProxyCodeResponse.getBody();
        assertTrue(Arrays.equals(qrCode,qrProxyCode));
    }

    /**
     * Method that first creates a ShortUrl, then makes a GET request with a mocked IP
     * to check that geolocalizaton module works correctly.
     * @throws Exception
     */
    @Test
    public void testGeolocationIP() throws Exception {
        //Create short url
        HttpHeaders qrHeaders = new HttpHeaders();
        qrHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
        body.add("url","http://www.pistachosoft.com/");
        HttpEntity<?> qrEntity = new HttpEntity<>(body,qrHeaders);
        ResponseEntity<String> qrResponse = new TestRestTemplate().exchange(
                "http://localhost:" + this.port + "/qr",
                HttpMethod.POST,
                qrEntity,
                String.class);
        assertEquals(HttpStatus.CREATED, qrResponse.getStatusCode());
        JSONObject json = new JSONObject(qrResponse.getBody());
        assertEquals(json.get("target"),"http://www.pistachosoft.com/");
        assertEquals(json.get("hash"),"8511a654");

        //Test geoIP
        String id = json.getString("hash");
        mockMvc.perform(get("/l{id}", id).with(new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setRemoteAddr("64.233.160.1");
                return request;
            }
        }).accept(MediaType.APPLICATION_JSON));
    }

}