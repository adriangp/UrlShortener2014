package urlshortener2014.goldenbrown.reachableurl;

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
public class ReachableURLControllerTests {
	
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
	 * @param url parameter "url" of the service
	 * @return a ResponseEntity of the test request
	 */
	private ResponseEntity<String> performTestRequest(String url){
		return new TestRestTemplate().getForEntity(
				"http://localhost:" + this.port + "/reachableurl" + "?url="+url, String.class);
	}

	@Test
	public void test_ReachableUrl_WellFormedAndReachable_200OK() throws Exception {
		ResponseEntity<String> entity = performTestRequest("http://www.unizar.es");
		assertEquals(HttpStatus.OK, entity.getStatusCode());
	}
	
	@Test
	public void test_ReachableUrl_WellFormedWithoutWWWAndReachable_200OK() throws Exception {
		ResponseEntity<String> entity = performTestRequest("http://unizar.es");
		assertEquals(HttpStatus.OK, entity.getStatusCode());
	}
	
	@Test
	public void test_ReachableUrl_WithoutProtocol_400BadRequest() throws Exception {
		ResponseEntity<String> entity = performTestRequest("www.unizar.es");
		assertEquals(HttpStatus.BAD_REQUEST, entity.getStatusCode());
	}
	
	@Test
	public void test_ReachableUrl_WithoutProtocolAndWWW_400BadRequest() throws Exception {
		ResponseEntity<String> entity = performTestRequest("unizar.es");
		assertEquals(HttpStatus.BAD_REQUEST, entity.getStatusCode());
	}
	
	@Test
	public void test_ReachableUrl_NotReachableBecauseOfRedirection_404NotFound() throws Exception {
		ResponseEntity<String> entity = performTestRequest("http://wikipedia.org/wiki/URL_redirection/");
		assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
	}
	
	// NOTE: This test assumess that port 54433 is not being used.
	@Test
	public void test_ReachableUrl_ConnectionRefusal_408RequestTimeout() throws Exception {
		ResponseEntity<String> entity = performTestRequest("http://localhost:54433/");
		assertEquals(HttpStatus.REQUEST_TIMEOUT, entity.getStatusCode());
	}
}