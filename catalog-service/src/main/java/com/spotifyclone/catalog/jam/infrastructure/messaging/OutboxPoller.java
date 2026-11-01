package com.spotifyclone.catalog.jam.infrastructure.messaging;

import com.spotifyclone.catalog.jam.application.port.JamEventPublisher;
import com.spotifyclone.catalog.jam.application.port.JamWorkerLock;
import com.spotifyclone.catalog.jam.domain.model.JamId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Polls unprocessed outbox rows and publishes them (transactional outbox,
 * POC-4). Publishing and marking-processed are separate steps, giving
 * at-least-once delivery: a crash between them re-sends the event.
 *
 * <p>Across instances a single leader polls: every scheduled tick tries the
 * Postgres advisory lock (ADR-003, POC-6) and non-leaders stand down, so rows
 * are not double-published by every replica. Rows are fetched in bounded
 * batches; a publish failure is logged and the row stays unprocessed for the
 * next cycle.
 */
@Component
public class OutboxPoller {

    private static final Logger log = LoggerFactory.getLogger(OutboxPoller.class);

    /** Fixed key for the cross-instance "outbox poller leader" lock. */
    private static final JamId POLLER_LOCK_ID =
            new JamId(UUID.fromString("00000000-0000-0000-0000-0000000000ff"));

    private final OutboxJpaRepository repository;
    private final JamEventPublisher publisher;
    private final TransactionTemplate transactionTemplate;
    private final JamWorkerLock workerLock;

    public OutboxPoller(
            OutboxJpaRepository repository,
            JamEventPublisher publisher,
            PlatformTransactionManager transactionManager,
            JamWorkerLock workerLock) {
        this.repository = repository;
        this.publisher = publisher;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.workerLock = workerLock;
    }

    @Scheduled(fixedDelayString = "${jam.outbox.poll-delay-ms:1000}")
    public void poll() {
        try {
            workerLock.withLock(POLLER_LOCK_ID, this::pollOnce);
        } catch (RuntimeException e) {
            // Lock backend unreachable (or not the leader's turn) — stay quiet,
            // the next tick retries. Not publishing is the safe failure mode.
            log.warn("outbox poll skipped: {}", e.getMessage());
        }
    }

    /** Publishes one bounded batch of unprocessed rows; returns the count published. */
    public int pollOnce() {
        List<OutboxJpaEntity> unprocessed = transactionTemplate.execute(status ->
                repository.findTop100ByProcessedAtIsNullOrderByCreatedAtAsc());
        int published = 0;
        for (OutboxJpaEntity event : unprocessed) {
            try {
                publisher.publish(event.getEventType(), event.getPayload());
                markProcessed(event.getId());
                published++;
            } catch (RuntimeException e) {
                // Leave unprocessed so a later poll retries it.
                log.warn("outbox event {} (type {}) not published: {}",
                        event.getId(), event.getEventType(), e.getMessage());
            }
        }
        return published;
    }

    private void markProcessed(UUID eventId) {
        transactionTemplate.executeWithoutResult(status ->
                repository.findById(eventId).ifPresent(event -> {
                    event.setProcessedAt(OffsetDateTime.now());
                    repository.save(event);
                }));
    }
}
