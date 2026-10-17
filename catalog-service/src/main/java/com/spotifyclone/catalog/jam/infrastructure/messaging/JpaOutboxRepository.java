package com.spotifyclone.catalog.jam.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spotifyclone.catalog.jam.application.port.OutboxRepository;
import com.spotifyclone.catalog.jam.domain.event.JamEvent;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * JPA adapter for the {@link OutboxRepository} port. Serializes each domain
 * event to a durable outbox row; the poller later publishes and marks it
 * processed. This replaces the Phase-4 no-op placeholder.
 */
@Component
public class JpaOutboxRepository implements OutboxRepository {

    private final OutboxJpaRepository repository;
    private final ObjectMapper objectMapper;

    public JpaOutboxRepository(OutboxJpaRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void append(JamEvent event) {
        repository.save(new OutboxJpaEntity(
                UUID.randomUUID(),
                event.getClass().getSimpleName(),
                serialize(event),
                OffsetDateTime.now()));
    }

    private String serialize(JamEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("cannot serialize event " + event.getClass(), e);
        }
    }
}
