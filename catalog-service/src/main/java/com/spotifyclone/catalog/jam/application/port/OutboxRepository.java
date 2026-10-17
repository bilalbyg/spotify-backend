package com.spotifyclone.catalog.jam.application.port;

import com.spotifyclone.catalog.jam.domain.event.JamEvent;

/**
 * Outbound port for the durable event log (transactional outbox, POC-4). The
 * Phase-5 adapter writes events atomically with the aggregate save and a
 * poller publishes them; the application layer only appends.
 */
public interface OutboxRepository {

    void append(JamEvent event);
}
