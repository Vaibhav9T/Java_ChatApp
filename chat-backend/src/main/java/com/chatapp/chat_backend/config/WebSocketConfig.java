package com.chatapp.chat_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // This is the endpoint your Next.js frontend will connect to
        // setAllowedOriginPatterns("*") prevents CORS blocks during local development
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // "/topic" is for broadcasting messages to subscribed clients (the chat room)
        registry.enableSimpleBroker("/topic");
        
        // "/app" is the prefix for messages sent FROM the client TO the server
        registry.setApplicationDestinationPrefixes("/app");
    }
}