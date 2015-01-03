package urlshortener2014.oldBurgundy.web.websocket.sponsor;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;



import urlshortener2014.oldBurgundy.repository.sponsor.SponsorWork;
import urlshortener2014.oldBurgundy.repository.sponsor.WorksRepositorySponsor;

public class SponsorHandler extends TextWebSocketHandler {

	private static final Logger logger = LoggerFactory.getLogger(SponsorHandler.class);

	@Autowired
	WorksRepositorySponsor worksRepository;

    @Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception{
		logger.info("Connected with id " + session.getId());
	}

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
		
		String[] msg = message.getPayload().split(" ");
		
		switch(msg.length){
			case 1:
				logger.info("Solicitated shorturl " + msg[0].trim() + " with id " + session.getId());
				this.worksRepository.addIncomingWork(new SponsorWork(msg[0].trim(),session));
				break;
			case 2:
				logger.info("Url" + msg[0].trim() + " with id " + session.getId());
			try {
				session.sendMessage(new TextMessage("ok::" + msg[0].trim()));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
				break;
			default:
				logger.info("Solicitated shorturl " + message.getPayload() + " with id " + session.getId());
				try {
					session.sendMessage(new TextMessage("error::" + 400));
				} catch (IOException e) {
				}
		}		
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception{
		logger.info("Disconnected with id " + session.getId());
    }
}