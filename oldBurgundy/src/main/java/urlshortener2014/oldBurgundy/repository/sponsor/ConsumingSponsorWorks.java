package urlshortener2014.oldBurgundy.repository.sponsor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import urlshortener2014.oldBurgundy.web.rest.validator.Url;

public class ConsumingSponsorWorks implements Runnable{

	private static final Logger logger = LoggerFactory.getLogger(ConsumingSponsorWorks.class);
	
	private WorksRepositorySponsor worksRepository;
	
	public ConsumingSponsorWorks(WorksRepositorySponsor worksRepository){
		this.worksRepository = worksRepository;
	}

	@Override
	public void run() {
		while(true){
			SponsorWork work = this.worksRepository.takeIncomingWork();
			logger.info("Requested new short for uri " + work.getUrl() + " shorUrl " + work.getShortUrl());
			
			this.worksRepository.addPendingWork(work);
			/*try{
				ResponseEntity<?> response = (new RestTemplate()).postForEntity("http://localhost:8080/validator/" + work.getId(), new Url(work.getUrl(), null), null);
				if(response.getStatusCode().value() == 200){
					
				}
				else{
					//
				}
			}
			catch(HttpClientErrorException e){
				System.out.println(e.getStatusCode().toString());
			}*/
			
		}
	}

}