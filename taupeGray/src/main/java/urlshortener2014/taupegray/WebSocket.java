package urlshortener2014.taupegray;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import urlshortener2014.taupegray.sponsor.WebSocketSponsorHandler;

@Configuration
@EnableWebSocket
public class WebSocket implements WebSocketConfigurer {
	
	/**
	 * Registers websocket handlers
	 */
	@Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(myHandler(), "/sponsor");
    }
	
	/**
	 * Creates the websocket handler.
	 */
    @Bean
    public WebSocketHandler myHandler() {
        return new WebSocketSponsorHandler();
    }
}
