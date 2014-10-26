package urlshortener2014.common.domain;

import java.net.URI;

public class ShortURL {

	private String hash;
	private String target;
	private URI uri;
	
	public ShortURL(String hash, String target, URI uri) {
		this.hash = hash;
		this.target = target;
		this.uri = uri;
	}


	public String getHash() {
		return hash;
	}
	
	public String getTarget() {
		return target;
	}


	public URI getUri() {
		return uri;
	}

}
