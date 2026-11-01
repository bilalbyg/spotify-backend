package com.spotifyclone.apigateway.security;

import org.junit.jupiter.api.Test;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WebSocketAuthFilterTest {

    private final JwtService jwtService = mock(JwtService.class);
    private final WebSocketAuthFilter filter = new WebSocketAuthFilter(jwtService);

    @Test
    void nonWsPath_isNotUnauthorized() {
        ServerHttpRequest request = MockServerHttpRequest.get("/api/v1/songs").build();
        assertThat(filter.isUnauthorized(request)).isFalse();
    }

    @Test
    void wsPathWithoutToken_isUnauthorized() {
        ServerHttpRequest request = MockServerHttpRequest.get("/ws/jam").build();
        assertThat(filter.isUnauthorized(request)).isTrue();
    }

    @Test
    void wsPathWithInvalidToken_isUnauthorized() {
        when(jwtService.isValid("bad")).thenReturn(false);
        ServerHttpRequest request = MockServerHttpRequest.get("/ws/jam")
                .header("Authorization", "Bearer bad")
                .build();
        assertThat(filter.isUnauthorized(request)).isTrue();
    }

    @Test
    void wsPathWithValidToken_isNotUnauthorized() {
        when(jwtService.isValid("good")).thenReturn(true);
        ServerHttpRequest request = MockServerHttpRequest.get("/ws/jam")
                .header("Authorization", "Bearer good")
                .build();
        assertThat(filter.isUnauthorized(request)).isFalse();
    }

    // --- Browser WebSocket clients cannot set headers; the token may arrive
    // --- as a ?token= query parameter instead.

    @Test
    void wsPathWithValidTokenInQueryParam_isNotUnauthorized() {
        when(jwtService.isValid("good")).thenReturn(true);
        ServerHttpRequest request = MockServerHttpRequest.get("/ws/jam?token=good").build();
        assertThat(filter.isUnauthorized(request)).isFalse();
    }

    @Test
    void wsPathWithInvalidTokenInQueryParam_isUnauthorized() {
        when(jwtService.isValid("bad")).thenReturn(false);
        ServerHttpRequest request = MockServerHttpRequest.get("/ws/jam?token=bad").build();
        assertThat(filter.isUnauthorized(request)).isTrue();
    }

    @Test
    void wsPathWithOnlyQueryParam_forwardsAuthorizationHeaderDownstream() {
        when(jwtService.isValid("good")).thenReturn(true);
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/ws/jam?token=good"));
        AtomicReference<ServerHttpRequest> downstream = new AtomicReference<>();

        filter.filter(exchange, ex -> {
            downstream.set(ex.getRequest());
            return Mono.empty();
        }).block();

        // The downstream service authenticates the handshake itself, so the
        // validated token must arrive as a standard Authorization header.
        assertThat(downstream.get().getHeaders().getFirst("Authorization")).isEqualTo("Bearer good");
    }

    @Test
    void wsPathWithHeaderToken_isForwardedUnchangedDownstream() {
        when(jwtService.isValid("good")).thenReturn(true);
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/ws/jam").header("Authorization", "Bearer good"));
        AtomicReference<ServerHttpRequest> downstream = new AtomicReference<>();

        filter.filter(exchange, ex -> {
            downstream.set(ex.getRequest());
            return Mono.empty();
        }).block();

        assertThat(downstream.get().getHeaders().getFirst("Authorization")).isEqualTo("Bearer good");
    }
}
