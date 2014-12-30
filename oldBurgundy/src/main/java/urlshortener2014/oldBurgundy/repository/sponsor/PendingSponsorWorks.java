package urlshortener2014.oldBurgundy.repository.sponsor;

import java.util.HashMap;

public class PendingSponsorWorks {

	private HashMap<Integer, SponsorWork> pendingWorks;
	
	protected PendingSponsorWorks(){
		this.pendingWorks = new HashMap<Integer, SponsorWork>(1000);
	}
	
	protected synchronized void add(SponsorWork work){
		this.pendingWorks.put(work.getId(), work);
	}
	
	protected synchronized SponsorWork remove(int id){
		return this.pendingWorks.remove(id);
	}
}
