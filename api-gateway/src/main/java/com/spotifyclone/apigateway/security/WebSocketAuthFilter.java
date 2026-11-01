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
 *
 * <p>Browser WebSocket clients cannot set request headers, so the token is also
 * accepted as a {@code ?token=} query parameter. Once validated, the token is
 * always forwarded downstream as a standard {@code Authorization: Bearer}
 * header — the downstream service authenticates the handshake itself instead of
 * trusting a gateway-injected identity header.
 */
@Component
public class WebSocketAuthFilter implements GlobalFilter {

    private static final String TOKEN_PARAM = "token";

    private final JwtService jwtService;

    public WebSocketAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        if (isUnauthorized(request)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        String token = resolveToken(request);
        if (token != null && request.getHeaders().getFirst("Authorization") == null) {
            request = request.mutate().header("Authorization", "Bearer " + token).build();
            return chain.filter(exchange.mutate().request(request).build());
        }
        return chain.filter(exchange);
    }

    /** Package-private for testing: true when the WS upgrade must be rejected. */
    boolean isUnauthorized(ServerHttpRequest request) {
        String path = request.getPath().value();
        if (!path.startsWith("/ws/")) {
            return false; // not a WebSocket route
        }
        String token = resolveToken(request);
        return token == null || !jwtService.isValid(token);
    }

    /** Bearer header wins; falls back to the {@code ?token=} query parameter. */
    private String resolveToken(ServerHttpRequest request) {
        String auth = request.getHeaders().getFirst("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            return auth.substring(7);
        }
        return request.getQueryParams().getFirst(TOKEN_PARAM);
    }
}
