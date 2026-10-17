package com.spotifyclone.catalog.jam.infrastructure.messaging;

import com.spotifyclone.catalog.jam.application.port.JamEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Phase-5 test: the outbox poller publishes unprocessed rows and marks them
 * processed (POC-4, at-least-once).
 */
@DataJpaTest
@ActiveProfiles("test")
class OutboxPollerTest {

    @Autowired
    private OutboxJpaRepository repository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private RecordingPublisher publisher;
    private OutboxPoller poller;

    private static final class RecordingPublisher implements JamEventPublisher {
        final List<String> published = new ArrayList<>();

        @Override
        public void publish(String eventType, String payload) {
            published.add(eventType);
        }
    }

    @BeforeEach
    void setUp() {
        publisher = new RecordingPublisher();
        poller = new OutboxPoller(repository, publisher, transactionManager);
    }

    @Test
    void pollOnce_publishesAndMarksProcessed() {
        repository.saveAndFlush(new OutboxJpaEntity(
                UUID.randomUUID(), "TrackQueued", "{\"sessionId\":\"x\"}", OffsetDateTime.now()));

        int published = poller.pollOnce();

        assertThat(published).isEqualTo(1);
        assertThat(publisher.published).containsExactly("TrackQueued");
        assertThat(repository.findByProcessedAtIsNullOrderByCreatedAtAsc()).isEmpty();
    }

    @Test
    void pollOnce_failure_leavesRowUnprocessed() {
        repository.saveAndFlush(new OutboxJpaEntity(
                UUID.randomUUID(), "TrackQueued", "{\"sessionId\":\"x\"}", OffsetDateTime.now()));

        // Failing publisher: throws on publish.
        JamEventPublisher failing = new JamEventPublisher() {
            @Override
            public void publish(String eventType, String payload) {
                throw new RuntimeException("broker down");
            }
        };
        poller = new OutboxPoller(repository, failing, transactionManager);

        int published = poller.pollOnce();

        assertThat(published).isEqualTo(0);
        assertThat(repository.findByProcessedAtIsNullOrderByCreatedAtAsc()).hasSize(1);
    }
}
