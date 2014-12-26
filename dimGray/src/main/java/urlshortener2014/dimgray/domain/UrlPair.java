package urlshortener2014.dimgray.domain;

import java.net.URI;

public class UrlPair {
	
	private String url;
	private URI shortenedUrl;
	
	public UrlPair(String url, URI shortenedUrl) {
		this.url = url;
		this.shortenedUrl = shortenedUrl;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public URI getShortenedUrl() {
		return shortenedUrl;
	}

	public void setShortenedUrl(URI shortenedUrl) {
		this.shortenedUrl = shortenedUrl;
	}

}
