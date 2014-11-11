package urlshortener2014.common.repository;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;
import static urlshortener2014.common.repository.fixture.ShortURLFixture.badUrl;
import static urlshortener2014.common.repository.fixture.ShortURLFixture.url1;
import static urlshortener2014.common.repository.fixture.ShortURLFixture.url2;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import urlshortener2014.common.domain.ShortURL;

public class ShortURLRepositoryTests {

	private EmbeddedDatabase db;
	private ShortURLRepository repository;
	private JdbcTemplate jdbc;

	@Before
	public void setup() {
		db = new EmbeddedDatabaseBuilder().setType(HSQL).addScript("schema-hsqldb.sql").build();
		jdbc = new JdbcTemplate(db);
		repository = new ShortURLRepositoryImpl(jdbc);
	}
	
	@Test
	public void thatSavePersistsTheShortURL() {
		assertNotNull(repository.save(url1()));
		assertSame(jdbc.queryForObject("select count(*) from SHORTURL", Integer.class), 1);
	}

	@Test
	public void thatSaveADuplicateHashIsSafelyIgnored() {
		repository.save(url1());
		assertNotNull(repository.save(url1()));
		assertSame(jdbc.queryForObject("select count(*) from SHORTURL", Integer.class), 1);
	}
	
	@Test
	public void thatErrorsInSaveReturnsNull() {
		assertNull(repository.save(badUrl()));
		assertSame(jdbc.queryForObject("select count(*) from SHORTURL", Integer.class), 0);
	}

	@Test
	public void thatFindByKeyReturnsAURL() {
		repository.save(url1());
		repository.save(url2());
		ShortURL su = repository.findByKey(url1().getHash());
		assertNotNull(su);
		assertSame(su.getHash(), url1().getHash());
	}

	@Test
	public void thatFindByKeyReturnsNullWhenFails() {
		repository.save(url1());
		assertNull(repository.findByKey(url2().getHash()));
	}

	@After
	public void shutdown() {
		db.shutdown();
	}
	
}
