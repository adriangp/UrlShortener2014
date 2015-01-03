package urlshortener2014.oldBurgundy.repository.sponsor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class IncomingSponsorWorks {

	private BlockingQueue<SponsorWork> incomingWorks;
	
	protected IncomingSponsorWorks(){
		this.incomingWorks = new ArrayBlockingQueue<SponsorWork>(1000, true);
	}
	
	protected synchronized boolean add(SponsorWork work){
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
