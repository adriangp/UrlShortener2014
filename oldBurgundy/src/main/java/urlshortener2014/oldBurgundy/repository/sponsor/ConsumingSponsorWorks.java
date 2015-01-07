package urlshortener2014.oldBurgundy.repository.sponsor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * Thread to process the short url petitions
 */
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
			
			logger.info("Short url petition taked shorUrl: '" + work.getShortUrl() + "' url: '" + work.getUrl() + "'");
			
			long time = System.currentTimeMillis() - work.getStamp();
			if (time < 10000){
				long waitTime = 10000 - time;
				logger.info("Waiting " + waitTime + " ms");
				try {
					Thread.sleep(waitTime);
				} catch (InterruptedException e1) {
				}
					
			}
			
			WebSocketSession ws = this.worksRepository.takeWaitingClient(work.getShortUrl());
			if(ws != null){
				try {
					logger.info("Send to client hash: '" + work.getShortUrl() + "' url: '" + work.getUrl() + "'");
					ws.sendMessage(new TextMessage(work.getUrl()));
				} catch (Exception e) {
				}
			}
			else if(work.getAttempt() < 3)
			{
				work.setAttempt(work.getAttempt() + 1);
				work.setStamp(System.currentTimeMillis());
				this.worksRepository.addIncomingWork(work);
			}			
		}
	}
}