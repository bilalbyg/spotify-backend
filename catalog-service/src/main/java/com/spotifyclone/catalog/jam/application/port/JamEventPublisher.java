package com.spotifyclone.catalog.jam.application.port;

/**
 * Outbound port for publishing outbox events to the message broker. The
 * Phase-5 Kafka adapter implements this; the poller only publishes raw type +
 * payload so the transport stays decoupled.
 */
public interface JamEventPublisher {

    void publish(String eventType, String payload);
}
