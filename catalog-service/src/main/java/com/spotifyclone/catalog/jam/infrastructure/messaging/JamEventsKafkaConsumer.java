package com.spotifyclone.catalog.jam.infrastructure.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.spotifyclone.catalog.jam.application.port.JamNotifier;
import com.spotifyclone.catalog.jam.domain.model.JamId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Kafka→STOMP bridge (completes the POC-5 realtime chain): consumes outbox
 * events published by {@link KafkaJamEventPublisher} and pushes them to the
 * session's STOMP topic via {@link JamNotifier}.
 *
 * <p>The consumer group is unique per instance
 * ({@code catalog-service-jam-ws-<random>}), so every catalog-service instance
 * receives every event and can push to its own locally-connected WebSocket
 * clients — the simple broker has no inter-instance transport.
 *
 * <p>The Kafka record key carries the event type; it is folded into the JSON
 * envelope as {@code "type"} so clients can dispatch on it. Malformed records
 * are logged and dropped (a poison record must not stall the live stream).
 */
@Component
public class JamEventsKafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(JamEventsKafkaConsumer.class);

    private final JamNotifier notifier;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JamEventsKafkaConsumer(JamNotifier notifier) {
        this.notifier = notifier;
    }

    @KafkaListener(
            topics = "jam-events",
            groupId = "${spring.application.name}-jam-ws-${random.uuid}")
    public void onMessage(
            @Header(KafkaHeaders.RECEIVED_KEY) String eventType,
            @Payload String payload) {
        if ("SessionCreated".equals(eventType)) {
            // SessionCreated carries the invite link; the host already received
            // it via the REST response and the topic must never leak it.
            return;
        }
        try {
            JsonNode tree = objectMapper.readTree(payload);
            String sessionId = tree.path("sessionId").path("value").asText(null);
            if (sessionId == null) {
                log.warn("jam event without sessionId dropped (type={})", eventType);
                return;
            }
            ((ObjectNode) tree).put("type", eventType);
            notifier.notifyRaw(new JamId(UUID.fromString(sessionId)), objectMapper.writeValueAsString(tree));
        } catch (Exception e) {
            log.warn("jam event dropped: type={}, error={}", eventType, e.getMessage());
        }
    }
}
