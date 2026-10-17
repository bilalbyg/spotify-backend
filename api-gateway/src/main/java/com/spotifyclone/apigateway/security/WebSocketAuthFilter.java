package com.spotifyclone.apigateway.security;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Validates the JWT on WebSocket upgrade requests (ADR-002). Only applies to
 * {@code /ws/**} paths; REST routes continue to delegate validation downstream.
 * An invalid or missing bearer token aborts with {@code 401}.
 */
@Component
public class WebSocketAuthFilter implements GlobalFilter {

    private final JwtService jwtService;

    public WebSocketAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (isUnauthorized(exchange.getRequest())) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange);
    }

    /** Package-private for testing: true when the WS upgrade must be rejected. */
    boolean isUnauthorized(ServerHttpRequest request) {
        String path = request.getPath().value();
        if (!path.startsWith("/ws/")) {
            return false; // not a WebSocket route
        }
        String auth = request.getHeaders().getFirst("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            return true;
        }
        return !jwtService.isValid(auth.substring(7));
    }
}
