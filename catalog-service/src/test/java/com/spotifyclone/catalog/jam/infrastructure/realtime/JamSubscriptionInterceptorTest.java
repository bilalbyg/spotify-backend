package com.spotifyclone.catalog.jam.infrastructure.realtime;

import com.spotifyclone.catalog.jam.application.port.JamRepository;
import com.spotifyclone.catalog.jam.domain.model.InviteLink;
import com.spotifyclone.catalog.jam.domain.model.JamSession;
import com.spotifyclone.catalog.jam.domain.model.UserId;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.support.MessageBuilder;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Topic authorization: anyone with a valid JWT may open the WebSocket, but only
 * session members may subscribe to {@code /topic/session/{id}} — otherwise a
 * leaked session id would allow silent eavesdropping on another jam.
 */
class JamSubscriptionInterceptorTest {

    private final JamRepository repository = mock(JamRepository.class);
    private final JamSubscriptionInterceptor interceptor = new JamSubscriptionInterceptor(repository);
    private final MessageChannel channel = mock(MessageChannel.class);

    @Test
    void member_maySubscribe() {
        UserId member = new UserId(UUID.randomUUID());
        JamSession session = JamSession.create(member, InviteLink.generate());
        when(repository.findById(session.id())).thenReturn(Optional.of(session));

        Message<?> message = subscribeMessage(session.id().value(), member.value());

        assertThat(interceptor.preSend(message, channel)).isSameAs(message);
    }

    @Test
    void nonMember_isRejected() {
        JamSession session = JamSession.create(new UserId(UUID.randomUUID()), InviteLink.generate());
        when(repository.findById(session.id())).thenReturn(Optional.of(session));

        Message<?> message = subscribeMessage(session.id().value(), UUID.randomUUID());

        assertThatThrownBy(() -> interceptor.preSend(message, channel))
                .isInstanceOf(MessageDeliveryException.class)
                .hasMessageContaining("not a member");
    }

    @Test
    void missingSessionIdentity_isRejected() {
        JamSession session = JamSession.create(new UserId(UUID.randomUUID()), InviteLink.generate());

        Message<?> message = subscribeMessage(session.id().value(), null);

        assertThatThrownBy(() -> interceptor.preSend(message, channel))
                .isInstanceOf(MessageDeliveryException.class);
    }

    @Test
    void unknownSession_isRejected() {
        UUID sessionId = UUID.randomUUID();
        when(repository.findById(new com.spotifyclone.catalog.jam.domain.model.JamId(sessionId)))
                .thenReturn(Optional.empty());

        Message<?> message = subscribeMessage(sessionId, UUID.randomUUID());

        assertThatThrownBy(() -> interceptor.preSend(message, channel))
                .isInstanceOf(MessageDeliveryException.class);
    }

    @Test
    void nonJamDestinations_passThrough() {
        Message<?> message = subscribeTo("/topic/now-playing", UUID.randomUUID());

        assertThat(interceptor.preSend(message, channel)).isSameAs(message);
    }

    private Message<?> subscribeMessage(UUID sessionId, UUID userId) {
        return subscribeTo("/topic/session/" + sessionId, userId);
    }

    private Message<?> subscribeTo(String destination, UUID userId) {
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create(SimpMessageType.SUBSCRIBE);
        accessor.setDestination(destination);
        if (userId != null) {
            accessor.setSessionAttributes(
                    Map.of(JamHandshakeInterceptor.USER_ID_ATTRIBUTE, userId.toString()));
        }
        return MessageBuilder.createMessage("", accessor.getMessageHeaders());
    }
}
