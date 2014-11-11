package urlshortener2014.common.repository;

import urlshortener2014.common.domain.ShortURL;

public interface ShortURLRepository {

	ShortURL findByKey(String id);

	ShortURL save(ShortURL su);

}
