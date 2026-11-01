package com.spotifyclone.catalog.jam.infrastructure.messaging;

import com.spotifyclone.catalog.jam.application.port.JamNotifier;
import com.spotifyclone.catalog.jam.domain.model.JamId;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Kafka→STOMP bridge: consumes outbox events from {@code jam-events} and pushes
 * them to the session's STOMP topic. The Kafka record key carries the event
 * type; the consumer folds it into the JSON envelope so clients can dispatch on
 * {@code type}, and routes by the {@code sessionId} inside the payload.
 */
class JamEventsKafkaConsumerTest {

    private final JamNotifier notifier = mock(JamNotifier.class);
    private final JamEventsKafkaConsumer consumer = new JamEventsKafkaConsumer(notifier);

    private static final UUID SESSION_ID = UUID.randomUUID();

    @Test
    void validEvent_notifiesSessionTopicWithTypedEnvelope() {
        String payload = """
                {"sessionId":{"value":"%s"},"track":{"value":"%s"},"actor":{"value":"%s"}}
                """.formatted(SESSION_ID, UUID.randomUUID(), UUID.randomUUID());

        consumer.onMessage("TrackQueued", payload);

        ArgumentCaptor<String> json = ArgumentCaptor.forClass(String.class);
        verify(notifier).notifyRaw(eq(new JamId(SESSION_ID)), json.capture());
        assertThat(json.getValue()).contains("\"type\":\"TrackQueued\"");
        assertThat(json.getValue()).contains(SESSION_ID.toString());
    }

    @Test
    void sessionCreated_isNeverBroadcast() {
        // SessionCreated carries the invite link; the topic has eavesdroppers we
        // cannot fully rule out, and the host already received the link via REST.
        String payload = """
                {"sessionId":{"value":"%s"},"host":{"value":"%s"},"inviteLink":{"value":"%s"}}
                """.formatted(SESSION_ID, UUID.randomUUID(), UUID.randomUUID());

        consumer.onMessage("SessionCreated", payload);

        verify(notifier, never()).notifyRaw(any(), any());
    }

    @Test
    void payloadWithoutSessionId_isDropped() {
        consumer.onMessage("SessionEnded", "{\"actor\":{\"value\":\"" + UUID.randomUUID() + "\"}}");

        verify(notifier, never()).notifyRaw(any(), any());
    }

    @Test
    void malformedPayload_isDroppedWithoutThrowing() {
        consumer.onMessage("TrackQueued", "{not json");

        verify(notifier, never()).notifyRaw(any(), any());
    }
}
