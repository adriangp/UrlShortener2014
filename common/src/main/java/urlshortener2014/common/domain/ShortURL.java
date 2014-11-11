package urlshortener2014.common.domain;

import java.net.URI;
import java.sql.Date;

public class ShortURL {

	private String hash;
	private String target;
	private URI uri;
	private Date created;
	private String owner;
	private Integer mode;
	
	
	public ShortURL(String hash, String target, URI uri, Date created, String owner, Integer mode) {
		this.hash = hash;
		this.target = target;
		this.uri = uri;
		this.created = created;
		this.owner = owner;
		this.mode = mode;
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

	public Date getCreated() {
		return created;
	}

	public String getOwner() {
		return owner;
	}

	public Integer getMode() {
		return mode;
	}
}
