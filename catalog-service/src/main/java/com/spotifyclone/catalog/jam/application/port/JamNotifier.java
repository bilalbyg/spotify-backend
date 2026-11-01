package com.spotifyclone.catalog.jam.application.port;

import com.spotifyclone.catalog.jam.domain.event.JamEvent;
import com.spotifyclone.catalog.jam.domain.model.JamId;

/**
 * Outbound port for real-time event notification (POC-5). The Phase-4
 * WebSocket/STOMP adapter implements this; the application layer only notifies.
 */
public interface JamNotifier {

    void notify(JamEvent event);

    /**
     * Broadcasts an already-serialized event envelope to the session's topic.
     * Used by the Kafka→STOMP bridge, which forwards outbox payloads verbatim
     * (with the event type folded in) instead of deserializing them back into
     * domain events.
     */
    void notifyRaw(JamId sessionId, String payload);
}
