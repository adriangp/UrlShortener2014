package urlshortener2014.oldBurgundy.web.websocket.sponsor;

import org.springframework.beans.factory.annotation.Autowired;

import urlshortener2014.oldBurgundy.repository.sponsor.AddNewSponsorWork;
import urlshortener2014.oldBurgundy.repository.sponsor.SponsorWork;
import urlshortener2014.oldBurgundy.repository.sponsor.WorksRepositorySponsor;



public class NewSponsorWork implements AddNewSponsorWork{
	
	@Autowired
	WorksRepositorySponsor worksRepository;

	@Override
	public boolean addIncomingWork(SponsorWork work) {
		return this.worksRepository.addIncomingWork(work);
	}


}
