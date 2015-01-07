package urlshortener2014.dimgray.domain;

import java.net.URI;
import java.sql.Date;

/**
	 * Clase para la comunicacion entre javascript y java, que contiene informacion o de un click
	 * o de una ShortURL, pero no de las dos. 
	 * @author Ivan Pinos y Paulo Pizarro
	 *
*/
public class InfoDB {

	private String hash;  		
	
	private Long id;
	private Date clickCreated;
	private String referrer;
	private String browser;
	private String platform;
	private String clickIp;
	private String clickCountry;
	private Boolean isClick;
	
	private String target;
	private URI uri;
	private String sponsor;
	private Date urlCreated;
	private String owner;
	private Integer mode;
	private Boolean safe;
	private String urlCountry;
	private String urlIp;
	private Boolean isUrl;

	
	public InfoDB(String hash, String target, URI uri, String sponsor,
			Date urlCreated, String owner, Integer mode, Boolean safe, String urlIp,
			String urlCountry, Boolean isUrl, Boolean isClick) {
		this.hash = hash;
		this.target = target;
		this.uri = uri;
		this.sponsor = sponsor;
		this.urlCreated = urlCreated;
		this.owner = owner;
		this.mode = mode;
		this.safe = safe;
		this.urlIp = urlIp;
		this.urlCountry = urlCountry;
		this.isUrl = isUrl;
		this.isClick = isClick;
	}

	public InfoDB(Long id, String hash, Date clickCreated, String referrer,
			String browser, String platform, String clickIp, String clickCountry,
			Boolean isUrl, Boolean isClick) {
		this.id = id;
		this.hash = hash;
		this.clickCreated = clickCreated;
		this.referrer = referrer;
		this.browser = browser;
		this.platform = platform;
		this.clickIp = clickIp;
		this.clickCountry = clickCountry;
		this.isUrl = isUrl;
		this.isClick = isClick;
	}
	
	public Long getId() {
		return id;
	}

	public String getHash() {
		return hash;
	}

	public Date getClickCreated() {
		return clickCreated;
	}

	public String getReferrer() {
		return referrer;
	}

	public String getBrowser() {
		return browser;
	}

	public String getPlatform() {
		return platform;
	}

	public String getClickIp() {
		return clickIp;
	}

	public String getClickCountry() {
		return clickCountry;
	}
	
	public String getTarget() {
		return target;
	}

	public URI getUri() {
		return uri;
	}

	public Date getUrlCreated() {
		return urlCreated;
	}

	public String getOwner() {
		return owner;
	}

	public Integer getMode() {
		return mode;
	}

	public String getSponsor() {
		return sponsor;
	}

	public Boolean getSafe() {
		return safe;
	}

	public String getUrlIp() {
		return urlIp;
	}

	public String getUrlCountry() {
		return urlCountry;
	}
	public Boolean getIsClick() {
		return isClick;
	}

	public Boolean getIsUrl() {
		return isUrl;
	}

}
