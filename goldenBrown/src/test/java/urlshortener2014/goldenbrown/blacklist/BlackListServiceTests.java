package urlshortener2014.goldenbrown.blacklist;

import org.apache.http.util.Asserts;
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
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port=0")
@DirtiesContext
public class BlackListServiceTests {
	
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
	
	
//	@Test
//	public void testIfBlacklistCacheIsWorking() throws Exception {
//		Boolean a = BlackListController.isBlackListed("google.es");
//		Boolean b = BlackListController.isBlackListed("google.es");
//		
//		assertSame("Cache is not working", a, b);
//	}
//	
//	@Test
//	public void testDNSNotBlackListed() throws Exception {
//		Boolean a = BlackListController.isBlackListed("google.es");
//		assertEquals("DNS is not working", false, a);
//	}
//	
//	/**
//	 * Blacklisted domains can be obtained from http://www.spamhaus.org/sbl/latest/
//	 * It's recommended to try first from Windows cmd:
//	 * 		nslookup <blacklisted_site>.zen.spamhaus.org.
//	 * @throws Exception
//	 */
//	@Test
//	public void testDNSBlackListed() throws Exception {
//		Boolean a = BlackListController.isBlackListed("edgecast.com");
//		assertEquals("DNS is not working", true, a);
//	}
}