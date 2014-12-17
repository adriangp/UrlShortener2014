package urlshortener2014.oldBurgundy.web.websocket.csv;

import java.io.IOException;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class CSVHandler extends TextWebSocketHandler {

    @Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception{
		System.out.println("conectado");
	}

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
		System.out.println("msg: " + message.getPayload());
		try {
			session.sendMessage(new TextMessage("respuestaaa"));
		} catch (IOException e) {
			
		}
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception{
		System.out.println("desconectado");
    }
}