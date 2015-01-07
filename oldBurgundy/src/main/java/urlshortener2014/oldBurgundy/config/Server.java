package urlshortener2014.oldBurgundy.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:/properties/server.properties")
public class Server {

	@Autowired
    Environment env;

	@Bean
    public String hostCore() {
        return env.getProperty("host.core") == null ? "localhost:8080" : env.getProperty("host.core");
    }

	@Bean
    public String hostValidator() {
        return env.getProperty("host.validator") == null ? "localhost:8080" : env.getProperty("host.validator");
    }

	@Bean
    public String hostCsv() {
        return env.getProperty("host.csv") == null ? "localhost:8080" : env.getProperty("host.csv");
    }

	@Bean
    public String hostSponsor() {
        return env.getProperty("host.sponsor") == null ? "localhost:8080" : env.getProperty("host.sponsor");
    }
}