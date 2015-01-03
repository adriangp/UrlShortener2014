package urlshortener2014.oldBurgundy.repository.sponsor;

import java.util.HashMap;

import org.springframework.web.socket.WebSocketSession;

public class PendingSponsorWorks {

	private HashMap<String, WebSocketSession> pendingWorks;
	
	protected PendingSponsorWorks(){
		this.pendingWorks = new HashMap<String, WebSocketSession>(1000);
	}
	
	protected synchronized void add(String shortUrl, WebSocketSession session){
		this.pendingWorks.put(shortUrl, session);
	}
	
	protected synchronized WebSocketSession remove(String id){
		return this.pendingWorks.remove(id);
	}
}
