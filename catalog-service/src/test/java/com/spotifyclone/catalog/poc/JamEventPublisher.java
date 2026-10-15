package com.spotifyclone.catalog.poc;

/**
 * POC fixture — the transport port the outbox poller publishes through. In
 * production this adapter would be a Kafka producer; for the POC it is an
 * in-memory recorder so the durability/retry behaviour is deterministic and
 * does not depend on a running broker.
 *
 * <p>NOT production code.
 */
public interface JamEventPublisher {

    /** Publishes an outbox event. Throws on failure (broker down, network). */
    void publish(JamOutboxEntity event);
}
