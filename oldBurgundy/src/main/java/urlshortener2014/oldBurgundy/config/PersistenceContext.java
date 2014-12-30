package urlshortener2014.oldBurgundy.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import urlshortener2014.common.repository.ClickRepository;
import urlshortener2014.common.repository.ClickRepositoryImpl;
import urlshortener2014.common.repository.ShortURLRepository;
import urlshortener2014.common.repository.ShortURLRepositoryImpl;
import urlshortener2014.oldBurgundy.repository.csv.ConsumingWorks;
import urlshortener2014.oldBurgundy.repository.csv.WorksRepository;
import urlshortener2014.oldBurgundy.repository.sponsor.ConsumingWorksSponsor;
import urlshortener2014.oldBurgundy.repository.sponsor.WorksRepositorySponsor;

@Configuration
public class PersistenceContext {

	@Autowired
    protected JdbcTemplate jdbc;

	@Bean
	ShortURLRepository shortURLRepository() {
		return new ShortURLRepositoryImpl(jdbc);
	}
 	
	@Bean
	ClickRepository clickRepository() {
		return new ClickRepositoryImpl(jdbc);
	}
	
	@Bean
	WorksRepository worksRepository(){
		WorksRepository worksRepository = new WorksRepository();
		new Thread(new ConsumingWorks(worksRepository)).start();
		return worksRepository;
	}
	
	@Bean
	WorksRepositorySponsor worksRepositorySponsor(){
		WorksRepositorySponsor worksRepository = new WorksRepositorySponsor();
		new Thread(new ConsumingWorksSponsor(worksRepository)).start();
		return worksRepository;
	}
}
