package com.spotifyclone.catalog.jam.infrastructure.realtime;

import com.spotifyclone.catalog.security.AuthenticatedUser;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * Copies the JWT-authenticated user into the WebSocket session attributes at
 * handshake time. The handshake request has already passed the servlet security
 * chain (a valid JWT is required for {@code /ws/jam/**}), so the
 * {@link SecurityContextHolder} holds the {@link AuthenticatedUser} principal on
 * this thread. From then on the STOMP session attribute is the single source of
 * actor identity for the command channel — command payloads are never trusted
 * for identity (Phase-7 anti-impersonation rule, extended to STOMP).
 */
@Component
public class JamHandshakeInterceptor implements HandshakeInterceptor {

    /** Session attribute key carrying the authenticated user's UUID (as string). */
    public static final String USER_ID_ATTRIBUTE = "jam.userId";

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !(authentication.getPrincipal() instanceof AuthenticatedUser user)
                || user.userId() == null) {
            return false; // defence in depth — the security chain already returns 401
        }
        attributes.put(USER_ID_ATTRIBUTE, user.userId().toString());
        return true;
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception) {
        // no-op
    }
}
