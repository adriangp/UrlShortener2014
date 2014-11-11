package urlshortener2014.common.repository;

import java.util.List;

import urlshortener2014.common.domain.Click;

public interface ClickRepository {

	List<Click> findByHash(String hash);

	Click save(Click su);

}
