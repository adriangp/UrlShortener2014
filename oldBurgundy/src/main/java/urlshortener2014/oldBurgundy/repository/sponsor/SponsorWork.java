package urlshortener2014.oldBurgundy.repository.sponsor;

import org.springframework.web.socket.WebSocketSession;

public class SponsorWork {
	
	private int id = -1;
	private String Url,shortUrl;
	private WebSocketSession session;
	private SponsorWorkStatus state;
		
	public SponsorWork(String Url,String shortUrl) {
		this.setUrl(Url);
		this.shortUrl = shortUrl;
	}
	
	public SponsorWork(String shortUrl,WebSocketSession session) {
		this.setSession(session);
		this.shortUrl = shortUrl;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getShortUrl() {
		return shortUrl;
	}

	public void setShortUrl(String shortUrl) {
		this.shortUrl = shortUrl;
	}

	public SponsorWorkStatus getState() {
		return state;
	}

	public void setState(SponsorWorkStatus state) {
		this.state = state;
	}

	public String getUrl() {
		return Url;
	}

	public void setUrl(String url) {
		Url = url;
	}

	public WebSocketSession getSession() {
		return session;
	}

	public void setSession(WebSocketSession session) {
		this.session = session;
	}
}
