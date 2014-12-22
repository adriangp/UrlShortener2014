package urlshortener2014.oldBurgundy.repository.csv;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class IncomingWorks {

	private BlockingQueue<Work> incomingWorks;
	private int nextId;
	
	protected IncomingWorks(){
		this.incomingWorks = new ArrayBlockingQueue<Work>(1000, true);
		nextId = 0;
	}
	
	protected synchronized boolean add(Work work){
		work.setId(++nextId);
		return work != null ? incomingWorks.offer(work) : false;
	}
	
	protected Work take(){
		try {
			return incomingWorks.take();
		} catch (InterruptedException e) {
			return null;
		}
	}
}
