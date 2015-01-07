package urlshortener2014.goldenbrown;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import urlshortener2014.goldenbrown.blacklist.BlackListService;


/**
 * Class that initializes a SpringApplication service.  Contains the main method of the application.
 * Also, the behaviour can be modified with the SpringApplicationBuilder class.
 * @author: Jorge,Javi,Gabi
 * @version: 08/01/2015
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan
@EnableCaching
public class Application extends SpringBootServletInitializer {
	/**
	 * Main method, starts the SpringApplication service
	 * @param args command line arguments
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}
	
	/**
	 * Method that allows to configure the SprintApplication
	 * @param application a configured SpringApplicationBuilder
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}

    @Bean
    public CacheManager cacheManager() {
    	return new ConcurrentMapCacheManager("blcache");
    }
    
    @Bean
    public BlackListService blackListService() {
    	return new BlackListService();
    }	
}