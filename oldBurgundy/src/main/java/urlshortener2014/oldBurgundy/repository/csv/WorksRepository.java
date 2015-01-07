package urlshortener2014.oldBurgundy.repository.csv;

/**
 * The repository to storage works
 */
public class WorksRepository {
	
	private IncomingWorks incomingWorks;
	private PendingWorks pendingWorks;
	
	/**
	 * New repository to storage works
	 */
	public WorksRepository(){
		this.incomingWorks = new IncomingWorks();
		this.pendingWorks = new PendingWorks();
	}

	/**
	 * Add new incoming work
	 * @param work The work to add
	 * @return 	<b>true</b> if the work was added <br>
	 * 			<b>false</b> if not
	 */
	public boolean addIncomingWork(Work work){
		return this.incomingWorks.add(work);
	}
	
	/**
	 * Take a work of the incoming works storage. It block the thread if there aren't incoming works
	 * @return The work that was taking or null if there was an error
	 */
	public Work takeIncomingWork(){
		return this.incomingWorks.take();
	}
	
	/**
	 * Add a new pending work
	 * @param work The work to add
	 */
	public void addPendingWork(Work work){
		this.pendingWorks.add(work);
	}
	
	/**
	 * Take a pending work
	 * @param id The <i>id</i> of the work
	 * @return The work removed
	 */
	public Work takePendingWork(int id){
		return this.pendingWorks.remove(id);
	}
}
