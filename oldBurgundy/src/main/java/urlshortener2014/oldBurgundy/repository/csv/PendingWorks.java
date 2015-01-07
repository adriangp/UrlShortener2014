package urlshortener2014.oldBurgundy.repository.csv;

import java.util.HashMap;

/**
 * Pending works storage
 */
public class PendingWorks {

	private HashMap<Integer, Work> pendingWorks;
	
	/**
	 * New pending works storage
	 */
	protected PendingWorks(){
		this.pendingWorks = new HashMap<Integer, Work>(1000);
	}
	
	/**
	 * Add a new work to this storage
	 * @param work
	 */
	protected synchronized void add(Work work){
		this.pendingWorks.put(work.getId(), work);
	}
	
	/**
	 * Remove a work of the storage
	 * @param id The <i>id</i> of the work to remove
	 * @return The work removed
	 */
	protected synchronized Work remove(int id){
		return this.pendingWorks.remove(id);
	}
}
