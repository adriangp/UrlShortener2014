package urlshortener2014.richcarmine.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import urlshortener2014.richcarmine.web.UrlShortenerControllerWithLogs;

@Configuration
@EnableWebSocket
public class NaiveWebSocketConfig implements WebSocketConfigurer{

    @Autowired
    UrlShortenerControllerWithLogs controller;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(myHandler(), "/ws/naivews");
    }

    @Bean
    public WebSocketHandler myHandler() {
        return controller. new MyHandler();
    }

}
