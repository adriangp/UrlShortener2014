package urlshortener2014.oldBurgundy.repository.sponsor;

import java.util.HashMap;

import org.springframework.web.socket.WebSocketSession;

/**
 * Waiting clients storage
 */
public class WaitingClients {

	private HashMap<String, WebSocketSession> waitingClients;
	
	/**
	 * New waiting clients storage
	 */
	protected WaitingClients(){
		this.waitingClients = new HashMap<String, WebSocketSession>(1000);
	}
	
	/**
	 * Add new waiting client
	 * @param shortUrl The hash of the url petition
	 * @param session The session of the client
	 */
	protected synchronized void add(String shortUrl, WebSocketSession session){
		this.waitingClients.put(shortUrl, session);
	}
	 
	/**
	 * Remove the session
	 * @param shortUrl The hash of the petition
	 * @return The session of the client
	 */
	protected synchronized WebSocketSession remove(String shortUrl){
		return this.waitingClients.remove(shortUrl);
	}
}
