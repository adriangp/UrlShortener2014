package urlshortener2014.oldBurgundy.web.websocket.csv;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.http.HttpStatus;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import urlshortener2014.oldBurgundy.repository.csv.Work;
import urlshortener2014.oldBurgundy.repository.csv.WorksRepository;

/**
 * Handler of the csv web socket
 */
public class CSVHandler extends TextWebSocketHandler {

	private static final Logger logger = LoggerFactory.getLogger(CSVHandler.class);

	@Autowired
	WorksRepository worksRepository;

	@Autowired
	private ServerProperties properties;

    @Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception{
		logger.info("Connected to csv web socket with id: " + session.getId());
	}

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
		
		String[] msg = message.getPayload().split(",");
		
		try{
			switch(msg.length){
				case 2:
					logger.info("Solicitated url: '" + msg[1].trim() + "' with id: " + session.getId());
					this.worksRepository.addIncomingWork(new Work(session, Integer.parseInt(msg[0].trim()), msg[1].trim(), null));
					break;
				case 3:
					logger.info("Solicitated url: '" + msg[1].trim() + "' sponsor: '" + msg[2].trim() + "' with id: " + session.getId());
					this.worksRepository.addIncomingWork(new Work(session, Integer.parseInt(msg[0].trim()), msg[1].trim(), msg[2].trim()));
					break;
				default:
					logger.info("Solicitated: '" + message.getPayload() + "' with id: " + session.getId());
					try {
						session.sendMessage(new TextMessage("error::" + HttpStatus.BAD_REQUEST.getReasonPhrase()));
					} catch (IOException e) {
					}
			}	
		} catch (NumberFormatException e){
			logger.info("Solicitated: '" + message.getPayload() + "' with id: " + session.getId());
			try {
				session.sendMessage(new TextMessage("error::" + HttpStatus.BAD_REQUEST.getReasonPhrase()));
			} catch (IOException e1) {
			}
		}
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception{
		logger.info("Disconnected to csv web socket with id: " + session.getId());
    }
}