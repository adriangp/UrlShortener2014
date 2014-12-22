package urlshortener2014.oldBurgundy.repository.csv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import urlshortener2014.oldBurgundy.web.rest.validator.Url;

public class ConsumingWorks implements Runnable{

	private static final Logger logger = LoggerFactory.getLogger(ConsumingWorks.class);
	
	private WorksRepository worksRepository;
	
	public ConsumingWorks(WorksRepository worksRepository){
		this.worksRepository = worksRepository;
	}

	@Override
	public void run() {
		while(true){
			Work work = this.worksRepository.takeIncomingWork();

			logger.info("Requested new short for uri " + work.getUrl() + " sponsor " + work.getSponsor());
			
			try{
				ResponseEntity<?> response = (new RestTemplate()).postForEntity("http://localhost:8080/validator/" + work.getId(), new Url(work.getUrl(), work.getSponsor()), null);
				if(response.getStatusCode().value() == 200){
					this.worksRepository.addPendingWork(work);
				}
				else{
					//
				}
			}
			catch(HttpClientErrorException e){
				System.out.println(e.getStatusCode().toString());
			}
			
		}
	}

}