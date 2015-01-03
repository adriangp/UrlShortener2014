package urlshortener2014.oldBurgundy.web.websocket.csv;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import urlshortener2014.oldBurgundy.repository.csv.Work;
import urlshortener2014.oldBurgundy.repository.csv.WorksRepository;

public class CSVHandler extends TextWebSocketHandler {

	private static final Logger logger = LoggerFactory.getLogger(CSVHandler.class);

	@Autowired
	WorksRepository worksRepository;

    @Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception{
		logger.info("Connected with id " + session.getId());
	}

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
		
		String[] msg = message.getPayload().split(",");
		
		switch(msg.length){
			case 1:
				logger.info("Solicitated url " + msg[0].trim() + " with id " + session.getId());
				this.worksRepository.addIncomingWork(new Work(session, msg[0].trim(), null));
				break;
			case 2:
				logger.info("Solicitated url " + msg[0].trim() + " - " + msg[1].trim() + " with id " + session.getId());
				this.worksRepository.addIncomingWork(new Work(session, msg[0].trim(), msg[1].trim()));
				break;
			default:
				logger.info("Solicitated url " + message.getPayload() + " with id " + session.getId());
				try {
					session.sendMessage(new TextMessage(this.worksRepository.takeIncomingWork().getShortUrl()));

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