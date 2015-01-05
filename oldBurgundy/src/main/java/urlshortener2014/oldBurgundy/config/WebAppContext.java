package urlshortener2014.oldBurgundy.config;

import org.apache.velocity.app.Velocity;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.config.EnableEntityLinks;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableEntityLinks
@ComponentScan(basePackages = {
        "urlshortener2014.oldBurgundy.web"
})
public class WebAppContext extends WebMvcConfigurerAdapter {

	public WebAppContext(){		
		Velocity.setProperty("file.resource.loader.path", "./src/main/resources/templates/");
		Velocity.init();
	}
	
}
