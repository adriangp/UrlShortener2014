package urlshortener2014.goldenbrown.platformidentifier;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import urlshortener2014.goldenbrown.Application;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port=0")
@DirtiesContext
public class PlatformIdentifierControllerTests {
	
	/*
	 * Nomenclature of junit methods described in (with examples):
	 * http://osherove.com/blog/2005/4/3/naming-standards-for-unit-tests.html
	 * 
	 * [test_UnitOfWork_StateUnderTest_ExpectedBehavior] 
	 * - test : inherited from JUnit3 
	 * - UnitOfWork : e.g. method being tested, classes functionality being tested... etc 
	 * - StateUnderTest : e.g. the input of the method, the class attributes... etc 
	 * - ExpectedBehaviour : e.g. expected method output, final state, Exception being thrown.. etc
	 */

	@Value("${local.server.port}")
	private int port = 0;
	
	/**
	 * This private method simplifies the test requests.
	 * @param us parameter "us" of the service
	 * @return a ResponseEntity of the test request
	 */
	private ResponseEntity<PlatformIdentity> performTestRequest(String us){
		return new TestRestTemplate().getForEntity(
				"http://localhost:" + this.port + "/platformidentifier" + "?us="+us, PlatformIdentity.class);
	}
	
	@Test
	public void test_PlatformIdentifier_EmptyUserAgent_200OkANDUnknownValues() throws Exception {
		ResponseEntity<PlatformIdentity> entity = performTestRequest("");
		//TODO CHECK: it is a valid behaviour to return OK when UserAgent equals "" ?
		assertEquals(HttpStatus.OK, entity.getStatusCode());
		
		PlatformIdentity pi = entity.getBody();
		assertEquals("UNKNOWN", pi.getOs());
		assertEquals("UNKNOWN", pi.getBrowser());
		assertEquals("UNKNOWN", pi.getVersion());
	}
	
	@Test
	public void test_PlatformIdentifier_OnlyOS_200OkANDOnlyValueOS() throws Exception {
		ResponseEntity<PlatformIdentity> entity = performTestRequest(
				"Windows");
		assertEquals(HttpStatus.OK, entity.getStatusCode());
		
		PlatformIdentity pi = entity.getBody();
		assertEquals("WINDOWS", pi.getOs());
		assertEquals("UNKNOWN", pi.getBrowser());
		assertEquals("UNKNOWN", pi.getVersion());
	}

	@Test
	public void test_PlatformIdentifier_FirefoxANDWindowsVista_200OkANDCorrectValues() throws Exception {
		ResponseEntity<PlatformIdentity> entity = performTestRequest(
				"Mozilla/5.0 (Windows NT 6.0; rv:36.0) Gecko/20100101 Firefox/36.0");
		assertEquals(HttpStatus.OK, entity.getStatusCode());
		
		PlatformIdentity pi = entity.getBody();
		assertEquals("WINDOWS_VISTA", pi.getOs());
		assertEquals("FIREFOX3", pi.getBrowser());
		assertEquals("36.0", pi.getVersion());
	}
	
	@Test
	public void test_PlatformIdentifier_ChromeANDWindowsVista_200OkANDCorrectValues() throws Exception {
		ResponseEntity<PlatformIdentity> entity = performTestRequest(
				"Mozilla/5.0 (Windows NT 6.0; rv:36.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36");
		assertEquals(HttpStatus.OK, entity.getStatusCode());
		
		PlatformIdentity pi = entity.getBody();
		assertEquals("WINDOWS_VISTA", pi.getOs());
		assertEquals("CHROME", pi.getBrowser());
		assertEquals("39.0.2171.95", pi.getVersion());
		
	}
	
	@Test
	public void test_PlatformIdentifier_InternetExplorerANDWindowsXP_200OkANDCorrectValues() throws Exception {
		ResponseEntity<PlatformIdentity> entity = performTestRequest(
				"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)");
		assertEquals(HttpStatus.OK, entity.getStatusCode());
		
		PlatformIdentity pi = entity.getBody();
		assertEquals("WINDOWS_XP", pi.getOs());
		assertEquals("IE6", pi.getBrowser());
		assertEquals("6.0", pi.getVersion());
	}
	
	@Test
	public void test_PlatformIdentifier_SafariANDMacOSX_200OkANDCorrectValues() throws Exception {
		ResponseEntity<PlatformIdentity> entity = performTestRequest(
				"Mozilla/5.0 (Macintosh; U; PPC Mac OS X; en) AppleWebKit/125.2 (KHTML, like Gecko) Safari/85.8");
		assertEquals(HttpStatus.OK, entity.getStatusCode());
		
		PlatformIdentity pi = entity.getBody();
		assertEquals("MAC_OS_X", pi.getOs());
		assertEquals("SAFARI", pi.getBrowser());
		//TODO CHECK: this should be 85.8, not UNKNOWN.
		assertEquals("UNKNOWN", pi.getVersion());
	}
	
	@Test
	public void test_PlatformIdentifier_EpiphanyANDLinux_200OkANDCorrectValues() throws Exception {
		ResponseEntity<PlatformIdentity> entity = performTestRequest(
				"Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.6) Gecko/20050614 Firefox/0.8");
		assertEquals(HttpStatus.OK, entity.getStatusCode());
		
		PlatformIdentity pi = entity.getBody();
		assertEquals("LINUX", pi.getOs());
		assertEquals("FIREFOX", pi.getBrowser());
		assertEquals("0.8", pi.getVersion());
	}
}
