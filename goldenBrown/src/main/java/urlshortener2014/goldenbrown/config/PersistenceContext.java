package urlshortener2014.goldenbrown.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import urlshortener2014.common.repository.ClickRepository;
import urlshortener2014.common.repository.ClickRepositoryImpl;
import urlshortener2014.common.repository.ShortURLRepository;
import urlshortener2014.common.repository.ShortURLRepositoryImpl;
/**
 * Class that controls the persistency of the database of common
 * @author: Jorge,Javi,Gabi
 * @version: 08/01/2015
 */
@Configuration
public class PersistenceContext {

	//Jdbcd Object that connects with the database
	@Autowired
    protected JdbcTemplate jdbc;

	/**
	 * Creates a jdbc object of the ShortURLRepositoryImpls class in project's common.
	 * This allows to use the database with this jdbc and save URL information.
	 */
	@Bean
	ShortURLRepository shortURLRepository() {
		return new ShortURLRepositoryImpl(jdbc);
	}
	
 	/**
 	 * Creates a jdbc object of the ClickRepositoryImpl class in project's common.
 	 * This allows to use the database with this jdbc and save click information.
 	 */
	@Bean
	ClickRepository clickRepository() {
		return new ClickRepositoryImpl(jdbc);
	}
	
}
