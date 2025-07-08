package com.ayyappa.hotelbooking.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/queue", "/all", "/topic"); // Added /topic for broader compatibility
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user"); // Add this for user-specific messages
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // In production, specify exact origins
                .withSockJS()
                .setStreamBytesLimit(512 * 1024) // Set streaming buffer limit (512KB)
                .setHttpMessageCacheSize(1000) // Set message cache size
                .setDisconnectDelay(30 * 1000); // Set disconnect delay (30 seconds)
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.setMessageSizeLimit(128 * 1024) // Message size limit (128KB)
                .setSendBufferSizeLimit(512 * 1024) // Send buffer size limit (512KB)
                .setSendTimeLimit(20 * 1000); // Send time limit (20 seconds)
    }
}