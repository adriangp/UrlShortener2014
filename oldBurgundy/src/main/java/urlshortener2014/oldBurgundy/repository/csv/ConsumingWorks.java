package urlshortener2014.oldBurgundy.repository.csv;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.TextMessage;

import urlshortener2014.oldBurgundy.web.rest.validator.Url;

/**
 * Thread to process the works
 */
public class ConsumingWorks implements Runnable{
	
	String hostValidator;

	private static final Logger logger = LoggerFactory.getLogger(ConsumingWorks.class);
	
	private WorksRepository worksRepository;
	
	public ConsumingWorks(WorksRepository worksRepository, String hostValidator){
		this.worksRepository = worksRepository;
		this.hostValidator = hostValidator;
	}

	@Override
	public void run() {
		while(true){
			Work work = this.worksRepository.takeIncomingWork();

			logger.info("Work taked url: '" + work.getUrl() + "' sponsor: '" + work.getSponsor() + "'");
			
			boolean send = false;
			int i = 0;
			do{
				i++;
				try{
					ResponseEntity<?> response = (new RestTemplate()).postForEntity(hostValidator + "/validator/" + work.getId(), new Url(work.getUrl(), work.getSponsor()), null);
					if(send = response.getStatusCode().value() == 200){
						this.worksRepository.addPendingWork(work);
					}
				}
				catch(HttpClientErrorException e){
				}
			}
			while(i < 3 && !send);
			
			if(!send){
				try {
					work.getSession().sendMessage(new TextMessage("error::" + work.getLine() + "::" + HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()));
				} catch (IOException e) {
				}
			}
		}
	}

}