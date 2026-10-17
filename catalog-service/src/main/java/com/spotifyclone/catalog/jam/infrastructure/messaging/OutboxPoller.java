package com.spotifyclone.catalog.jam.infrastructure.messaging;

import com.spotifyclone.catalog.jam.application.port.JamEventPublisher;
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
 */
@Component
public class OutboxPoller {

    private final OutboxJpaRepository repository;
    private final JamEventPublisher publisher;
    private final TransactionTemplate transactionTemplate;

    public OutboxPoller(
            OutboxJpaRepository repository,
            JamEventPublisher publisher,
            PlatformTransactionManager transactionManager) {
        this.repository = repository;
        this.publisher = publisher;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Scheduled(fixedDelayString = "${jam.outbox.poll-delay-ms:1000}")
    public void poll() {
        pollOnce();
    }

    /** Publishes all currently-unprocessed rows; returns the count published. */
    public int pollOnce() {
        List<OutboxJpaEntity> unprocessed = transactionTemplate.execute(status ->
                repository.findByProcessedAtIsNullOrderByCreatedAtAsc());
        int published = 0;
        for (OutboxJpaEntity event : unprocessed) {
            try {
                publisher.publish(event.getEventType(), event.getPayload());
                markProcessed(event.getId());
                published++;
            } catch (RuntimeException e) {
                // Leave unprocessed so a later poll retries it.
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
