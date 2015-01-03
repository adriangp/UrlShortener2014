package urlshortener2014.oldBurgundy.repository.sponsor;

import java.util.HashMap;

public class PendingSponsorWorks {

	private HashMap<Integer, SessionClient> pendingWorks;
	
	protected PendingSponsorWorks(){
		this.pendingWorks = new HashMap<Integer, SessionClient>(1000);
	}
	
	protected synchronized void add(SessionClient work){
		this.pendingWorks.put(work.getId(), work);
	}
	
	protected synchronized SessionClient remove(String id){
		return this.pendingWorks.remove(id);
	}
}
