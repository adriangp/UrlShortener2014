package rest.validator;

import org.eclipse.persistence.jaxb.rs.MOXyJsonProvider;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

public class ApplicationConfig extends ResourceConfig {

	/**
     * Default constructor
     */
    public ApplicationConfig() {
    	this(new Uri());
    }


    /**
     * Main constructor
     * @param uri a provided address book
     */
    public ApplicationConfig(final Uri uri) {
    	register(ValidatorWebService.class);
    	register(MOXyJsonProvider.class);
    	register(CrossDomainFilter.class);
    	register(new AbstractBinder() {

			@Override
			protected void configure() {
				bind(uri).to(Uri.class);
			}});
	}	

}
