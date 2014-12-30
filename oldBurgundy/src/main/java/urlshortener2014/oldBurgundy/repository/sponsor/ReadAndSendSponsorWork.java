package urlshortener2014.oldBurgundy.repository.sponsor;

public interface ReadAndSendSponsorWork {

	public SponsorWork readPendingWork();
	
	public void sendWork(SponsorWork work);
	
}
