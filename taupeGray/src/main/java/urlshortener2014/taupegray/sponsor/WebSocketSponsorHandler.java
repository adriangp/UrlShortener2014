package urlshortener2014.taupegray.sponsor;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import urlshortener2014.common.domain.ShortURL;
import urlshortener2014.common.repository.ShortURLRepository;

@Component
public class WebSocketSponsorHandler extends TextWebSocketHandler {
	@Autowired
	private ShortURLRepository shortURLRepository;
	
	private static final Logger logger = LoggerFactory
			.getLogger(WebSocketSponsorHandler.class);
	
	/**
	 * Handles websocket conections.
	 */
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		logger.info("New connection: "+session.getId());
	}
	
	/**
	 * Handles websocket messages.
	 */
	@Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
		logger.info("Request from "+session.getId()+" : "+message.getPayload());
		switch(message.getPayload()) {
		case "":
			Object time = session.getAttributes().get("initialtime");
			if(time != null && System.currentTimeMillis()-((Long)time).longValue() > 10000) {
				trySendEndMessage(session, session.getAttributes().get("targetURL").toString());
			}
			else {
				trySendMessage(session, "wait");
			}
			break;
		default:
			ShortURL l = shortURLRepository.findByKey(message.getPayload());
			
			if(l != null) {
				session.getAttributes().put("targetURL", l.getTarget());
				session.getAttributes().put("initialtime", System.currentTimeMillis());
				trySendMessage(session, l.getSponsor());
			}
			else {
				trySendMessage(session, "wrong hash");
			}
		}
    }
	
	protected void trySendMessage(WebSocketSession session, String message) {
		try {
			session.sendMessage(new TextMessage(message));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected void trySendEndMessage(WebSocketSession session, String message) {
		trySendMessage(session,message);
		try {
			session.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
