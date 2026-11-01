package com.spotifyclone.catalog.jam.infrastructure.realtime;

import com.spotifyclone.catalog.jam.application.port.JamRepository;
import com.spotifyclone.catalog.jam.domain.model.JamId;
import com.spotifyclone.catalog.jam.domain.model.JamSession;
import com.spotifyclone.catalog.jam.domain.model.UserId;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Topic authorization for the jam realtime channel. Authentication alone is not
 * enough: a valid JWT plus a leaked session id must not grant eavesdropping on
 * another session's events. Only session members may subscribe to
 * {@code /topic/session/{id}}; every other destination passes through.
 *
 * <p>The subscriber identity is the handshake-authenticated user stored in the
 * STOMP session by {@link JamHandshakeInterceptor} — never a client-supplied
 * value.
 */
@Component
public class JamSubscriptionInterceptor implements ChannelInterceptor {

    private static final String SESSION_TOPIC_PREFIX = "/topic/session/";

    private final JamRepository jamRepository;

    public JamSubscriptionInterceptor(JamRepository jamRepository) {
        this.jamRepository = jamRepository;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(message);
        if (accessor.getMessageType() != SimpMessageType.SUBSCRIBE) {
            return message;
        }
        String destination = accessor.getDestination();
        if (destination == null || !destination.startsWith(SESSION_TOPIC_PREFIX)) {
            return message;
        }
        UserId subscriber = authenticatedUser(accessor);
        JamId sessionId = parseSessionId(destination);
        Optional<JamSession> session = jamRepository.findById(sessionId);
        if (session.isEmpty() || !session.get().members().contains(subscriber)) {
            throw new MessageDeliveryException(
                    "user is not a member of session " + sessionId.value());
        }
        return message;
    }

    private UserId authenticatedUser(SimpMessageHeaderAccessor accessor) {
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
        Object userId = sessionAttributes == null
                ? null
                : sessionAttributes.get(JamHandshakeInterceptor.USER_ID_ATTRIBUTE);
        if (userId == null) {
            throw new MessageDeliveryException("no authenticated user in the STOMP session");
        }
        return new UserId(UUID.fromString(userId.toString()));
    }

    private JamId parseSessionId(String destination) {
        try {
            return new JamId(UUID.fromString(destination.substring(SESSION_TOPIC_PREFIX.length())));
        } catch (IllegalArgumentException e) {
            throw new MessageDeliveryException("malformed session topic: " + destination);
        }
    }
}
