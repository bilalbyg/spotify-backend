package com.spotifyclone.catalog.jam.infrastructure.realtime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * STOMP-over-WebSocket configuration for the jam realtime channel. Clients
 * connect to {@code /ws/jam} and subscribe to {@code /topic/session/{id}} for
 * session events (POC-5). The gateway terminates the connection and validates
 * the JWT before proxying here (ADR-002). Allowed origins come from the same
 * {@code ALLOWED_ORIGINS} whitelist the gateway uses — no wildcard.
 */
@Configuration
@EnableWebSocketMessageBroker
public class JamWebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JamHandshakeInterceptor jamHandshakeInterceptor;
    private final JamSubscriptionInterceptor jamSubscriptionInterceptor;
    private final String[] allowedOrigins;

    public JamWebSocketConfig(
            JamHandshakeInterceptor jamHandshakeInterceptor,
            JamSubscriptionInterceptor jamSubscriptionInterceptor,
            @Value("${jam.ws.allowed-origins:${ALLOWED_ORIGINS:http://localhost:3001,http://localhost:3002}}")
            String allowedOrigins) {
        this.jamHandshakeInterceptor = jamHandshakeInterceptor;
        this.jamSubscriptionInterceptor = jamSubscriptionInterceptor;
        this.allowedOrigins = allowedOrigins.split(",");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/jam")
                .addInterceptors(jamHandshakeInterceptor)
                .setAllowedOriginPatterns(allowedOrigins);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(jamSubscriptionInterceptor);
    }
}
