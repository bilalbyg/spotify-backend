package com.spotifyclone.catalog.jam.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spotifyclone.catalog.jam.domain.event.JamEvent;
import com.spotifyclone.catalog.jam.domain.model.JamId;
import com.spotifyclone.catalog.jam.domain.model.TrackId;
import com.spotifyclone.catalog.jam.domain.model.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Phase-5 integration test: the JPA outbox adapter persists domain events
 * durably (POC-4).
 */
@DataJpaTest
@ActiveProfiles("test")
class JpaOutboxRepositoryTest {

    @Autowired
    private OutboxJpaRepository jpaRepository;

    private JpaOutboxRepository outbox;

    @BeforeEach
    void setUp() {
        outbox = new JpaOutboxRepository(jpaRepository, new ObjectMapper());
    }

    @Test
    void append_persistsAnUnprocessedRow() {
        JamEvent event = new JamEvent.TrackQueued(
                new JamId(UUID.randomUUID()), new TrackId(UUID.randomUUID()), new UserId(UUID.randomUUID()));

        outbox.append(event);

        List<OutboxJpaEntity> unprocessed = jpaRepository.findByProcessedAtIsNullOrderByCreatedAtAsc();
        assertThat(unprocessed).hasSize(1);
        assertThat(unprocessed.get(0).getEventType()).isEqualTo("TrackQueued");
        assertThat(unprocessed.get(0).getPayload()).contains("sessionId");
        assertThat(unprocessed.get(0).getProcessedAt()).isNull();
    }
}
