package urlshortener2014.goldenbrown.blacklist;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

import urlshortener2014.common.domain.ShortURL;
import urlshortener2014.goldenbrown.Application;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port=0")
@DirtiesContext
public class BlackListControllerTests {
	
	@Autowired
	BlackListService blackListService;
	
	@Autowired
	CacheManager cacheManager;
	
	
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
	 * Blacklisted domains can be obtained from http://www.spamhaus.org/sbl/latest/
	 * It's recommended to try first from Windows cmd:
	 * 		nslookup <blacklisted_site>.zen.spamhaus.org
	 */
	private String urlBlackListed = "http://104.221.128.0";
	
	private String urlNotBlackListed = "http://www.google.com";

	private ResponseEntity<?> performTestRequestOnShortener(String url){
		return new TestRestTemplate().getForEntity(
					"http://localhost:"+this.port+"/blacklist/onshortener/?url={url}",
					null,
					url);
	}
	
	private ResponseEntity<?> performTestRequestOnRedirect(String url, Date date, boolean safe) throws java.text.ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = sdf.format(date);
		return new TestRestTemplate().getForEntity(
					"http://localhost:"+this.port+"/blacklist/onredirectto/?url={url}&date={date}&safe={safe}",
					null,
					url,
					dateString,
					safe);
	}
	
	
	@Test
	public void testDomainNotBlackListedOnShortener() throws Exception {
		ResponseEntity<?> entity = performTestRequestOnShortener(urlNotBlackListed);
		assertEquals(HttpStatus.OK, entity.getStatusCode());
	}
	
	@Test
	public void testDomainOnRedirect() throws Exception {
		final long hoursInMillis = 60L * 60L * 1000L;
		Date now = new Date();
		Date before = new Date(now.getTime() + 
		                        (-3L * hoursInMillis));
		ResponseEntity<?> entity;
		
		// Asks for a URL which is actually safe, it was checked recently, and it was considered safe then
		entity = performTestRequestOnRedirect(urlNotBlackListed, now, true);
		// It shouldn't be checked again, and it should return OK 
		assertEquals(HttpStatus.OK,entity.getStatusCode());
		
		// Asks for a URL which is actually safe, it was checked recently, and it was considered not-safe then
		entity = performTestRequestOnRedirect(urlNotBlackListed, now, false);
		// It shouldn't be checked again, and it should return LOCKED 
		assertEquals(HttpStatus.LOCKED, entity.getStatusCode());
		
		
		// Asks for a URL which is actually safe and it was checked long time ago 
		// (so it doesn't matter if it was considered safe or not)
		entity = performTestRequestOnRedirect(urlNotBlackListed, before, true);
		// It should be checked again, and it should return CREATED
		assertEquals(HttpStatus.OK,entity.getStatusCode());
		
		
		// Asks for a URL which is actually not-safe and it was checked long time ago 
		// (so it doesn't matter if it was considered safe or not)
		entity = performTestRequestOnRedirect(urlBlackListed, before, true);
		// It should be checked again, and it should return LOCKED
		assertEquals(HttpStatus.LOCKED, entity.getStatusCode());
	}
	

	@Test
	public void testDomainBlackListedOnShortener() throws Exception {
		ResponseEntity<?> entity = performTestRequestOnShortener(urlBlackListed);
		assertEquals(HttpStatus.LOCKED,entity.getStatusCode());
	}
	
	@Test
	public void testCache() throws Exception {
		blackListService.isBlackListed(urlNotBlackListed);
		ValueWrapper check_wrap = cacheManager.getCache("blcache").get(urlNotBlackListed);
		assertTrue(check_wrap != null);
	}
}