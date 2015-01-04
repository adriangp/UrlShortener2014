package urlshortener2014.oldBurgundy.repository.sponsor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

public class ConsumingSponsorWorks implements Runnable{

	private static final Logger logger = LoggerFactory.getLogger(ConsumingSponsorWorks.class);
	
	private WorksRepositorySponsor worksRepository;
	
	public ConsumingSponsorWorks(WorksRepositorySponsor worksRepository){
		this.worksRepository = worksRepository;
	}

	@Override
	public void run() {
		while(true){

			SponsorWork workBloq = this.worksRepository.takeIncomingWork();
			logger.info("Requested new short for uri " + workBloq.getUrl() + " shorUrl " + workBloq.getShortUrl());
			
			long tiempo = System.currentTimeMillis() - workBloq.getStamp();
			if (tiempo < 10000){
				logger.info("Not current time ");
				workBloq.setState(workBloq.getState() + 1);
				if (workBloq.getState() < 3){
					this.worksRepository.addIncomingWork(workBloq);
					try {
						Thread.sleep(10000-tiempo);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				else
					logger.info("Client not conected, erase work ");
					
			}
			else{
				WebSocketSession ws = this.worksRepository.takePendingWork(workBloq.getShortUrl());
				if(ws!=null){
					try {
						logger.info("Send to ws " + workBloq.getUrl());
						ws.sendMessage(new TextMessage(workBloq.getUrl()));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.info("Not valid id ");
					}
				}
				else
				{
					workBloq.setState(workBloq.getState() + 1);
					workBloq.setStamp(System.currentTimeMillis());
					this.worksRepository.addIncomingWork(workBloq);
				}
			}

			//se redireccionara en breves
			//take 
			//syste curent actual resta...tiempo bloquin queue
			//10 seg - ese tiempo...tiempo bloquin queue
			//theat.sleep
			//pasan 10 seg
			//.get (id) consulta hashmap busca idurlcorta...contador veces metido
			//null estampillamos tiemp de nuevo contador ++ 
			//valiable session url larga
			
		}
	}

}