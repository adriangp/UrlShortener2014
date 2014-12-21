package urlshortener2014.oldBurgundy.repository.csv;

public interface ReadAndSendWork {

	public Work readPendingWork();
	
	public void sendWork(Work work);
	
}
