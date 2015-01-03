package urlshortener2014.oldBurgundy.web.websocket.csv;

import org.springframework.beans.factory.annotation.Autowired;

import urlshortener2014.oldBurgundy.repository.csv.AddNewWork;
import urlshortener2014.oldBurgundy.repository.csv.Work;
import urlshortener2014.oldBurgundy.repository.csv.WorksRepository;




public class NewWork implements AddNewWork{
	
	@Autowired
	WorksRepository worksRepository;

	@Override
	public boolean addIncomingWork(Work work) {
		return this.worksRepository.addIncomingWork(work);
	}

}
