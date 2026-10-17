package com.spotifyclone.apigateway.security;

import org.junit.jupiter.api.Test;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;

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
}
