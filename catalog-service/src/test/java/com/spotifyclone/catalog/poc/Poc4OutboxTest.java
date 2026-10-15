package com.spotifyclone.catalog.poc;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * POC-4 — transactional outbox.
 *
 * <p>Invariant under test: once a domain change commits, its event is not lost,
 * even if the publisher fails between commit and publish. The outbox must
 * durably record the event in the same transaction as the domain change, and a
 * later poll must retry any unprocessed row.
 *
 * <p><b>Expected red</b> against {@link JamOutboxService} (Slice 1, inline
 * publish): when the publisher fails after the domain commit, the event is
 * gone and {@link JamEventPublisher} never receives it. Slice 2 writes to the
 * outbox atomically and turns this green.
 */
@SpringBootTest
@ActiveProfiles("poc")
class Poc4OutboxTest {

    @Autowired
    private JamOutbox outbox;

    @Autowired
    private RecordingJamEventPublisher publisher;

    @AfterEach
    void tearDown() {
        publisher.reset();
    }

    @Test
    void committedDomainChange_IsNeverLost_AcrossPublisherFailure() {
        UUID sessionId = outbox.createSession();
        UUID trackId = UUID.randomUUID();

        // Publisher down during the append.
        publisher.setFailMode(true);
        outbox.appendTrack(sessionId, trackId);

        // Publisher recovers and the poller runs — the event must be delivered.
        publisher.setFailMode(false);
        outbox.publishPending();

        assertEquals(1, publisher.publishedCount(),
                "a committed domain change must never lose its event");
    }

    @Test
    void publishFailure_LeavesRowUnprocessed_ForRetry() {
        UUID sessionId = outbox.createSession();
        outbox.appendTrack(sessionId, UUID.randomUUID());

        // First poll fails (broker down) — the row stays unprocessed.
        publisher.setFailMode(true);
        int first = outbox.publishPending();

        // Second poll succeeds.
        publisher.setFailMode(false);
        int second = outbox.publishPending();

        assertEquals(0, first, "failing poll must publish nothing");
        assertEquals(1, second, "recovered poll must publish the pending event");
        assertEquals(1, publisher.publishedCount());
        assertEquals(0, outbox.unprocessedCount());
        assertEquals(1, outbox.processedCount());
    }
}
