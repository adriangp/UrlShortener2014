package urlshortener2014.oldBurgundy.repository.sponsor;

import org.springframework.web.socket.WebSocketSession;

public class WorksRepositorySponsor {
	
	private IncomingSponsorWorks incomingWorks;
	private PendingSponsorWorks pendingWorks;
	
	public WorksRepositorySponsor(){
		this.incomingWorks = new IncomingSponsorWorks();
		this.pendingWorks = new PendingSponsorWorks();
	}

	public boolean addIncomingWork(SponsorWork work){
		return this.incomingWorks.add(work);
	}
	
	public SponsorWork takeIncomingWork(){
		return this.incomingWorks.take();
	}
	
	public void addPendingWork(String shortUrl, WebSocketSession session){
		this.pendingWorks.add(shortUrl, session);
	}
	
	public WebSocketSession takePendingWork(String string){
		return this.pendingWorks.remove(string);
	}
}
