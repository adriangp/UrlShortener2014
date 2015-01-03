package urlshortener2014.oldBurgundy.repository.sponsor;

import org.springframework.web.socket.WebSocketSession;

public class SponsorWork {
	
	private long stamp;
	private String url, shortUrl;
	private int state;
		
	public SponsorWork(String url, String shortUrl) {
		this.url = url;
		this.shortUrl = shortUrl;
		this.stamp = System.currentTimeMillis();
		this.state = 0;
	}
	
	public SponsorWork(String Url, WebSocketSession shortUrl) {
		this.setUrl(Url);
	}
	
	public SponsorWork(String shortUrl) {
		this.shortUrl = shortUrl;
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

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}


}