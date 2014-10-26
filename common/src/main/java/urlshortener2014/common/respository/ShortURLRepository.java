package urlshortener2014.common.respository;

import urlshortener2014.common.domain.ShortURL;

public interface ShortURLRepository {

	ShortURL findByKey(String id);

	ShortURL save(ShortURL su);

}
