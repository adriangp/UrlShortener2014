package urlshortener2014.oldBurgundy.repository.sponsor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class IncomingSponsorWorks {

	private BlockingQueue<SponsorWork> incomingWorks;
	private int nextId;
	
	protected IncomingSponsorWorks(){
		this.incomingWorks = new ArrayBlockingQueue<SponsorWork>(1000, true);
		nextId = 0;
	}
	
	protected synchronized boolean add(SponsorWork work){
		work.setId(++nextId);
		return work != null ? incomingWorks.offer(work) : false;
	}
	
	protected SponsorWork take(){
		try {
			return incomingWorks.take();
		} catch (InterruptedException e) {
			return null;
		}
	}
}
