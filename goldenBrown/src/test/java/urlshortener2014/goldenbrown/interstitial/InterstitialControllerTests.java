package urlshortener2014.goldenbrown.interstitial;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import urlshortener2014.goldenbrown.Application;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port=0")
@DirtiesContext
public class InterstitialControllerTests {

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
	 * 
	 * @param us
	 *            parameter "us" of the service
	 * @return a ResponseEntity of the test request
	 */
	private ResponseEntity<String> performTestRequest(String targetURL, String interstitialURL) {
		return new TestRestTemplate().getForEntity("http://localhost:" + this.port + "/interstitial" + "?targetURL="
				+ targetURL + "&interstitialURL=" + interstitialURL, String.class);
	}

	private final String SAMPLE_TARGET = "http://www.unizar.es";
	private final String SAMPLE_INTERSTITIAL = "http://add.unizar.es/add/campusvirtual";

	@Test
	public void test_Interstitial_PageTitleANDContentMediaType_200OkANDCorrectValues() throws Exception {
		ResponseEntity<String> entity = performTestRequest(SAMPLE_TARGET, SAMPLE_INTERSTITIAL);

		assertEquals(HttpStatus.OK, entity.getStatusCode());
		assertTrue("Wrong body (title doesn't match):\n" + entity.getBody(),
				entity.getBody().contains("<title>Redirecting"));
		assertEquals("Wrong content type:\n" + entity.getHeaders().getContentType(),
				MediaType.valueOf("text/html;charset=UTF-8"), entity.getHeaders().getContentType());
	}

	@Test
	public void test_Interstitial_UrlsReferencesOnGeneratedInterstitial_BodyContainsReferences() throws Exception {
		ResponseEntity<String> entity = performTestRequest(SAMPLE_TARGET, SAMPLE_INTERSTITIAL);
		
		assertTrue("Interstitial does not contain target url reference", entity.getBody().contains(SAMPLE_TARGET));
		assertTrue("Interstitial does not contain interstitial url reference",
				entity.getBody().contains(SAMPLE_INTERSTITIAL));
	}
	
	@Test
	public void test_Interstitial_EmptyInterstitialUrl_200Ok() throws Exception {
		ResponseEntity<String> entity = performTestRequest("SAMPLE_TARGET", "");
		// This is OK because html response do what is called to do:
		// show an interstitial with no publicity ("") and redirects to the targetUrl 
		assertEquals(HttpStatus.OK, entity.getStatusCode());
	}
	
	@Test
	public void test_Interstitial_EmptyUrls_200Ok() throws Exception {
		ResponseEntity<String> entity = performTestRequest("", "");
		// This is OK because html response do what is called to do:
		// show an interstitial with no publicity ("") and redirects to "" 
		// (that is the interstitial again, provoking a 10 second redirection loop :D)
		assertEquals(HttpStatus.OK, entity.getStatusCode());
	}
}