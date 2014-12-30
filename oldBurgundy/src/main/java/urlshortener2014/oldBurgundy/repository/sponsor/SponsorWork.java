package urlshortener2014.oldBurgundy.repository.sponsor;

import org.springframework.web.socket.WebSocketSession;

public class SponsorWork {
	
	private int id = -1;
	private String url, shortUrl;
	private WorkStatus state;
		
	public SponsorWork(String url) {
		this.url = url;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getShortUrl() {
		return shortUrl;
	}

	public void setShortUrl(String shortUrl) {
		this.shortUrl = shortUrl;
	}

	public WorkStatus getState() {
		return state;
	}

	public void setState(WorkStatus state) {
		this.state = state;
	}
}
