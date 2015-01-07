package urlshortener2014.oldBurgundy.repository.csv;

import org.springframework.web.socket.WebSocketSession;

/**
 * URL Shortener work
 */
public class Work {
	
	private int id = -1;
	private WebSocketSession session;
	private int line;
	private String url, shortUrl, sponsor;
	
	/**
	 * New URL Shortener work
	 * @param session The session of the client
	 * @param line The line of the document
	 * @param url The url to be shortened
	 * @param sponsor The url of the sponsor
	 */
	public Work(WebSocketSession session, int line, String url, String sponsor) {
		this.session = session;
		this.line = line;
		this.url = url;
		this.sponsor = sponsor;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public WebSocketSession getSession() {
		return session;
	}

	public void setSession(WebSocketSession session) {
		this.session = session;
	}

	public int getLine() {
		return line;
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

	public String getSponsor() {
		return sponsor;
	}

	public void setSponsor(String sponsor) {
		this.sponsor = sponsor;
	}
}
