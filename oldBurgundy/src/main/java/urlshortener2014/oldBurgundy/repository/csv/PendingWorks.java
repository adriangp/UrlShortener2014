package urlshortener2014.oldBurgundy.repository.csv;

import java.util.HashMap;

public class PendingWorks {

	private HashMap<Integer, Work> pendingWorks;
	
	protected PendingWorks(){
		this.pendingWorks = new HashMap<Integer, Work>(1000);
	}
	
	protected synchronized void add(Work work){
		this.pendingWorks.put(work.getId(), work);
	}
	
	protected synchronized Work remove(int id){
		return this.pendingWorks.remove(id);
	}
}
