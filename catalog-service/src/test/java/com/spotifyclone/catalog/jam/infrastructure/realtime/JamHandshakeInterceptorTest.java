package com.spotifyclone.catalog.jam.infrastructure.realtime;

import com.spotifyclone.catalog.security.AuthenticatedUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The handshake runs after the servlet security chain, so the JWT-authenticated
 * principal is available in the {@link SecurityContextHolder}. The interceptor
 * copies it into the WebSocket session attributes — the only identity the STOMP
 * command channel will ever trust. An unauthenticated handshake is refused here
 * as defence in depth (the security chain already returns 401 earlier).
 */
class JamHandshakeInterceptorTest {

    private final JamHandshakeInterceptor interceptor = new JamHandshakeInterceptor();

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void authenticatedUser_isCopiedIntoSessionAttributes() {
        UUID userId = UUID.randomUUID();
        authenticateAs(userId);
        Map<String, Object> attributes = new HashMap<>();

        boolean proceed = interceptor.beforeHandshake(request(), response(), null, attributes);

        assertThat(proceed).isTrue();
        assertThat(attributes).containsEntry(JamHandshakeInterceptor.USER_ID_ATTRIBUTE, userId.toString());
    }

    @Test
    void unauthenticatedHandshake_isRefused() {
        Map<String, Object> attributes = new HashMap<>();

        boolean proceed = interceptor.beforeHandshake(request(), response(), null, attributes);

        assertThat(proceed).isFalse();
        assertThat(attributes).isEmpty();
    }

    private void authenticateAs(UUID userId) {
        var auth = new UsernamePasswordAuthenticationToken(
                new AuthenticatedUser(userId, "jam@example.com", "USER"),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private ServletServerHttpRequest request() {
        return new ServletServerHttpRequest(new MockHttpServletRequest());
    }

    private ServletServerHttpResponse response() {
        return new ServletServerHttpResponse(new MockHttpServletResponse());
    }
}
