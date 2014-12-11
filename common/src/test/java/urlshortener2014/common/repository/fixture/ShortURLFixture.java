package urlshortener2014.common.repository.fixture;

import urlshortener2014.common.domain.ShortURL;

public class ShortURLFixture {

	public static ShortURL url1() {
		return new ShortURL("1", null, null, null, null, null, null, false,
				null, null);
	}

	public static ShortURL url2() {
		return new ShortURL("2", null, null, null, null, null, null, false,
				null, null);
	}

	public static ShortURL badUrl() {
		return new ShortURL(null, null, null, null, null, null, null, false,
				null, null);
	}

	public static ShortURL urlSponsor() {
		return new ShortURL("3", null, null, "sponsor", null, null, null,
				false, null, null);
	}

	public static ShortURL urlSafe() {
		return new ShortURL("4", null, null, "sponsor", null, null, null, true,
				null, null);
	}
}
