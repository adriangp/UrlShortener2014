package urlshortener2014.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import urlshortener2014.richcarmine.Application;
import urlshortener2014.richcarmine.domain.csv.CSVContent;
import urlshortener2014.richcarmine.domain.csv.ResponseData;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;

import static org.junit.Assert.*;
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

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private MockMvc mockMvc;

    private File testFile;

    /**
     * Set up a MockMVC of this application.
     */
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.ctx).build();
    }

    @Before
    public void loadTestFile() throws IOException {
        testFile = folder.newFile("someCSVFile.csv");
        PrintWriter writer = new PrintWriter(testFile);
        writer.println("http://youtube.com");
        writer.println("smth random");
        writer.close();
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
     * Test the rest endpoint located at /rest/csv and verifies
     * its response after uploading a .csv file
     */
    @Test
    public void testCSVRestService() {
        RestTemplate restTemplate = new RestTemplate();
        String uri = "http://localhost:" + this.port + "/rest/csv";
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("file", new FileSystemResource(testFile.getAbsolutePath()));
        HttpHeaders imageHeaders = new HttpHeaders();
        HttpEntity<MultiValueMap<String, Object>> imageEntity = new HttpEntity<>(map, imageHeaders);
        ResponseEntity<ResponseData> responseEntity = restTemplate.exchange(uri,
                HttpMethod.POST,
                imageEntity,
                ResponseData.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        ResponseData responseData = responseEntity.getBody();
        assertNotNull(responseData.getCsv().get(0).getShortURL());
        assertNull(responseData.getCsv().get(1).getShortURL());
    }


    /**
     * Test the websocket located at /ws/naivews and verify that every single
     * message corresponds its Java Class as well as verifying the final result
     * @throws Exception
     */
    @Test
    public void testCSVWSService() throws Exception {
        String lastMessageReceived = "";
        StandardWebSocketClient client = new StandardWebSocketClient();
        MyTestHandler handler = new MyTestHandler();
        WebSocketConnectionManager manager = new WebSocketConnectionManager(client,
                 handler,
                "ws://localhost:" + this.port + "/ws/naivews");
        manager.start();
        while(handler.running){
            Thread.sleep(200);
        }
        ObjectMapper mapper = new ObjectMapper();
        boolean isResponseData = true;
        try{
            ResponseData data = mapper.readValue(handler.lastMessage,ResponseData.class);
            assertNotNull(data.getCsv().get(0).getShortURL());
            assertNull(data.getCsv().get(1).getShortURL());
        } catch (UnrecognizedPropertyException e) {isResponseData = false;}

        assertTrue(isResponseData);
    }

    /**
     * Handler used to test the websocket
     */
    class MyTestHandler extends TextWebSocketHandler{
        private String lastMessage = "";
        private boolean running = true;
        ObjectMapper mapper = new ObjectMapper();

        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            try {
                Scanner s = new Scanner(testFile);
                while(s.hasNextLine()) session.sendMessage(new TextMessage(s.nextLine()));
                session.sendMessage(new TextMessage("<<EOF>>"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            synchronized (this){
                lastMessage = message.getPayload().trim();
                running = !lastMessage.contains("\"consumedTime\":");
                if(running){
                    try{
                        mapper.readValue(lastMessage,CSVContent.class);
                    } catch (UnrecognizedPropertyException e) { assertTrue(false); }
                }
            }
        }

        public String getLastMessage(){
            return lastMessage;
        }
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
        body.add("url", "http://www.pistachosoft.com/");
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
     * to check that geolocation module works correctly.
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
        RequestPostProcessor rpp = new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setRemoteAddr("64.233.160.2");
                return request;
            }
        };
        mockMvc.toString();
        mockMvc.perform(get("/l{id}", id).with(rpp).accept(MediaType.APPLICATION_JSON));
    }

}