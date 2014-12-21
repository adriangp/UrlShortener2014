package urlshortener2014.oldBurgundy.repository.csv;

public class WorksRepository {
	
	private IncomingWorks incomingWorks;
	private PendingWorks pendingWorks;
	
	public WorksRepository(){
		this.incomingWorks = new IncomingWorks();
		this.pendingWorks = new PendingWorks();
	}

	public boolean addIncomingWork(Work work){
		return this.incomingWorks.add(work);
	}
	
	public Work takeIncomingWork(){
		return this.incomingWorks.take();
	}
	
	public void addPendingWork(Work work){
		this.pendingWorks.add(work);
	}
	
	public Work takePendingWork(int id){
		return this.pendingWorks.remove(id);
	}
}
