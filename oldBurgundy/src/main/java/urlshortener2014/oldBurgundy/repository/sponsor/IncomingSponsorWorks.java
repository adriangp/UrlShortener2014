package urlshortener2014.oldBurgundy.repository.sponsor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Incoming sponsor works storage
 */
public class IncomingSponsorWorks {

	private BlockingQueue<SponsorWork> incomingWorks;

	/**
	 * New incoming sponsor works storage
	 */
	protected IncomingSponsorWorks(){
		this.incomingWorks = new ArrayBlockingQueue<SponsorWork>(1000, true);
	}
	
	/**
	 * Add new sponsor work
	 * @param work The new work
	 * @return 	<b>true</b> if the work was added <br>
	 * 			<b>false</b> if not
	 */
	protected synchronized boolean add(SponsorWork work){
		return work != null ? incomingWorks.offer(work) : false;
	}
	
	/**
	 * Take a work of the incoming sponsor works storage. It block the thread if there aren't incoming works
	 * @return The work that was taking or null if there was an error
	 */
	protected SponsorWork take(){
		try {
			return incomingWorks.take();
		} catch (InterruptedException e) {
			return null;
		}
	}
}
