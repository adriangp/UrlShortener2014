package urlshortener2014.common.repository.fixture;

import urlshortener2014.common.domain.ShortURL;

public class ShortURLFixture {
	
	public static ShortURL url1() {
		return new ShortURL("1",null,null,null,null,null);
	}

	public static ShortURL url2() {
		return new ShortURL("2",null,null,null,null,null);
	}

	public static ShortURL badUrl() {
		return new ShortURL(null,null,null,null,null,null);
	}
}
