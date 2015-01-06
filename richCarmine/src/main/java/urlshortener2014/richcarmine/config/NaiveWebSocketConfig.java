package urlshortener2014.richcarmine.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import urlshortener2014.common.domain.ShortURL;
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

//                          BROKER CONFIG
//    public void configureMessageBroker(MessageBrokerRegistry registry) {
//        registry.enableSimpleBroker("/response");
//        registry.setApplicationDestinationPrefixes("/ws"); //prefix
//    }
//
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        registry.addEndpoint("/ws/naivews").withSockJS();
//    }
}
