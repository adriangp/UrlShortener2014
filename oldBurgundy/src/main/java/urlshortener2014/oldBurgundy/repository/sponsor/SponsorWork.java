package urlshortener2014.oldBurgundy.repository.sponsor;

/**
 * URL petition
 */
public class SponsorWork {
	
	private long stamp;
	private String url, shortUrl;
	private int attempt;
	
	/**
	 * New url petition
	 * @param url
	 * @param shortUrl
	 */
	public SponsorWork(String url, String shortUrl) {
		this.url = url;
		this.shortUrl = shortUrl;
		this.stamp = System.currentTimeMillis();
		this.attempt = 0;
	}

	public String getShortUrl() {
		return shortUrl;
	}

	public void setShortUrl(String shortUrl) {
		this.shortUrl = shortUrl;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public long getStamp() {
		return stamp;
	}

	public void setStamp(long stamp) {
		this.stamp = stamp;
	}

	public int getAttempt() {
		return attempt;
	}

	public void setAttempt(int attempt) {
		this.attempt = attempt;
	}


}