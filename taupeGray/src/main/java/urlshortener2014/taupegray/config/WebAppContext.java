package urlshortener2014.taupegray.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.config.EnableEntityLinks;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableEntityLinks
@ComponentScan(basePackages = {
        "urlshortener2014.taupegray.web"
})
public class WebAppContext extends WebMvcConfigurerAdapter {

}
