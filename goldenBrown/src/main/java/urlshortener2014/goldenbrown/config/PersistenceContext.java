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
 * Clase que controla la persistencia de la base de datos del common
 * @author: Jorge,Javi,Gabi
 * @version: 08/01/2015
 */
@Configuration
public class PersistenceContext {

	//Objeto jdbc que se conecta con la BD
	@Autowired
    protected JdbcTemplate jdbc;

	/**
	 * Crea el objeto jdbc de la clase ShortURLRepositoryImpl en el common del proyecto para poder
	 * utilizar la BD con este jdbc y guardar informacion de las URL.
	 */
	@Bean
	ShortURLRepository shortURLRepository() {
		return new ShortURLRepositoryImpl(jdbc);
	}
 	/**
 	 * Crea el objeto jdbc de la clase ClickRepositoryImpl en el common del proyecto para poder
 	 * utilizar la BD cone ste jdbc y guardar informacion de los click.
 	 */
	@Bean
	ClickRepository clickRepository() {
		return new ClickRepositoryImpl(jdbc);
	}
	
}
