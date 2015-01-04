package urlshortener2014.goldenbrown;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * Esta clase inicializa el servicio llamando a SpringApplication. Contiene el main de la aplicacion
 * Tambien se puede modificar su comportamiento con la clase SpringApplicationBuilder.
 * @author: Jorge,Javi,Gabi
 * @version: 08/01/2015
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan
public class Application extends SpringBootServletInitializer {
	/**
	 * Metodo Main, invoca a SpringApplication para correr el servicio
	 * @param args 
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}
	
	/**
	 * Metodo para configurar la SpringApplication
	 * @param application hay que pasarle un SpringApplicationBuilder ya configurado.
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}

}