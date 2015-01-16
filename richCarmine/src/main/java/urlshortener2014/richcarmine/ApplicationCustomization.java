package urlshortener2014.richcarmine;

import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.ErrorPage;
import org.springframework.http.HttpStatus;

/**
 * Created by David Recuenco on 14/01/2015.
 */
public class ApplicationCustomization extends ServerProperties {

    /**
     * This will override any properties loaded by application.properties. Be
     * careful about this one
     */
    @Override
    public void customize(ConfigurableEmbeddedServletContainer container) {

        super.customize(container);
        container.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND,"/templates/error/404.html"));
        container.addErrorPages(new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR,"/templates/error/500.html"));
        container.addErrorPages(new ErrorPage("/templates/error/error.html"));
    }
}
