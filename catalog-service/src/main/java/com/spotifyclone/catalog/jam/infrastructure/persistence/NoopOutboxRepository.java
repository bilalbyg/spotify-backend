package com.spotifyclone.catalog.jam.infrastructure.persistence;

import com.spotifyclone.catalog.jam.application.port.OutboxRepository;
import com.spotifyclone.catalog.jam.domain.event.JamEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Placeholder {@link OutboxRepository} so the application context can boot
 * before Phase 5 delivers the real transactional outbox (POC-4). Logs events
 * instead of durably persisting them.
 */
@Component
public class NoopOutboxRepository implements OutboxRepository {

    private static final Logger log = LoggerFactory.getLogger(NoopOutboxRepository.class);

    @Override
    public void append(JamEvent event) {
        log.debug("outbox append (placeholder): {}", event);
    }
}
