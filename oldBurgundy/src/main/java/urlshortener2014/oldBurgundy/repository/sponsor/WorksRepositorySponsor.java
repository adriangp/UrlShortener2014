package urlshortener2014.oldBurgundy.repository.sponsor;

import org.springframework.web.socket.WebSocketSession;

/**
 * Sponsor work storage
 */
public class WorksRepositorySponsor {
	
	private IncomingSponsorWorks incomingWorks;
	private WaitingClients waitingClients;
	
	/**
	 * New sponsor work storage
	 */
	public WorksRepositorySponsor(){
		this.incomingWorks = new IncomingSponsorWorks();
		this.waitingClients = new WaitingClients();
	}

	/**
	 * Add new url petition
	 * @param work The work of the petiton
	 * @return 	<b>true</b> if the work was added <br>
	 * 			<b>false</b> if not
	 */
	public boolean addIncomingWork(SponsorWork work){
		return this.incomingWorks.add(work);
	}
	
	/**
	 * Take a work
	 * @return The work taked
	 */
	public SponsorWork takeIncomingWork(){
		return this.incomingWorks.take();
	}
	
	/**
	 * Add a client to wait the work
	 * @param shortUrl The hash of the work
	 * @param session The session of the client
	 */
	public void addWaitingClient(String shortUrl, WebSocketSession session){
		this.waitingClients.add(shortUrl, session);
	}
	
	/**
	 * Take a waiting client
	 * @param shortUrl The hash of the short url petition
	 * @return The session of the client
	 */
	public WebSocketSession takeWaitingClient(String shortUrl){
		return this.waitingClients.remove(shortUrl);
	}
}
