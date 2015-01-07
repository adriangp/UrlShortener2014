package urlshortener2014.oldBurgundy.repository.csv;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Incoming works storage
 */
public class IncomingWorks {

	private BlockingQueue<Work> incomingWorks;
	private int nextId;
	
	/**
	 * New incoming works storage
	 */
	protected IncomingWorks(){
		this.incomingWorks = new ArrayBlockingQueue<Work>(1000, true);
		nextId = 0;
	}
	
	/**
	 * Add new work
	 * @param work The new work
	 * @return 	<b>true</b> if the work was added <br/>
	 * 			<b>false</b> if not
	 */
	protected synchronized boolean add(Work work){
		work.setId(++nextId);
		return work != null ? incomingWorks.offer(work) : false;
	}
	
	/**
	 * Take a work of the incoming works storage. It block the thread if there aren't incoming works
	 * @return The work that was taking or null if there was an error
	 */
	protected Work take(){
		try {
			return incomingWorks.take();
		} catch (InterruptedException e) {
			return null;
		}
	}
}
