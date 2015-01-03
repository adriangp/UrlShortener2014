package urlshortener2014.oldBurgundy.repository.sponsor;

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
	
	public void addPendingWork(SessionClient work){
		this.pendingWorks.add(work);
	}
	
	public SessionClient takePendingWork(String string){
		return this.pendingWorks.remove(string);
	}
}
