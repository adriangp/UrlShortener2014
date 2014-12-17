package urlshortener2014.oldBurgundy.web.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import urlshortener2014.oldBurgundy.web.websocket.csv.CSVHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {


	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(csvHandler(), "/csv/ws");
		
	}

    @Bean
    public CSVHandler csvHandler() {
        return new CSVHandler();
    }

}