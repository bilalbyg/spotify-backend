package com.spotifyclone.catalog.poc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * POC fixture — <b>Slice 2 transactional-outbox implementation</b> behind the
 * {@link JamOutbox} seam.
 *
 * <p>{@link #appendTrack} writes the queue change <i>and</i> the outbox row in a
 * single transaction, so the event is durably recorded the moment the domain
 * change commits. {@link #publishPending} then drains unprocessed rows through
 * the {@link JamEventPublisher}; a publish failure leaves the row unprocessed
 * (for a later retry), so no event is ever lost. Publishing and marking are
 * separate steps, giving at-least-once delivery (a crash between them re-sends
 * the event — dedup is a downstream concern).
 *
 * <p><b>Slice 1 (red)</b> published inline with no durable record, so a publish
 * failure lost the event. NOT production code.
 */
@Service
public class JamOutboxService implements JamOutbox {

    private static final TypeReference<List<UUID>> UUID_LIST = new TypeReference<>() {
    };

    private static final int MAX_RETRIES = 25;

    private final JamSessionRepository sessionRepository;
    private final JamOutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;
    private final JamEventPublisher publisher;
    private final TransactionTemplate transactionTemplate;

    public JamOutboxService(
            JamSessionRepository sessionRepository,
            JamOutboxRepository outboxRepository,
            ObjectMapper objectMapper,
            JamEventPublisher publisher,
            PlatformTransactionManager transactionManager) {
        this.sessionRepository = sessionRepository;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
        this.publisher = publisher;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    public UUID createSession() {
        return transactionTemplate.execute(status ->
                sessionRepository.save(new JamSessionEntity(UUID.randomUUID(), "[]")).getId());
    }

    @Override
    public UUID appendTrack(UUID sessionId, UUID trackId) {
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            try {
                return transactionTemplate.execute(status -> {
                    // Domain change + outbox row in the SAME transaction.
                    JamSessionEntity session = require(sessionId);
                    List<UUID> queue = parse(session.getQueueJson());
                    queue.add(trackId);
                    session.setQueueJson(serialize(queue));
                    sessionRepository.save(session);

                    outboxRepository.save(new JamOutboxEntity(
                            UUID.randomUUID(), "track-added", trackId.toString(), OffsetDateTime.now()));
                    return trackId;
                });
            } catch (ObjectOptimisticLockingFailureException e) {
                if (attempt == MAX_RETRIES - 1) {
                    throw e;
                }
            }
        }
        throw new IllegalStateException("unreachable");
    }

    @Override
    public int publishPending() {
        List<JamOutboxEntity> unprocessed =
                outboxRepository.findByProcessedAtIsNullOrderByCreatedAtAsc();
        int published = 0;
        for (JamOutboxEntity event : unprocessed) {
            try {
                publisher.publish(event);
                markProcessed(event.getId());
                published++;
            } catch (RuntimeException e) {
                // Leave unprocessed so a later poll retries it.
            }
        }
        return published;
    }

    @Override
    public long unprocessedCount() {
        return transactionTemplate.execute(status -> outboxRepository.countByProcessedAtIsNull());
    }

    @Override
    public long processedCount() {
        return transactionTemplate.execute(status -> outboxRepository.countByProcessedAtIsNotNull());
    }

    private void markProcessed(UUID eventId) {
        transactionTemplate.executeWithoutResult(status ->
                outboxRepository.findById(eventId).ifPresent(event -> {
                    event.setProcessedAt(OffsetDateTime.now());
                    outboxRepository.save(event);
                }));
    }

    private JamSessionEntity require(UUID sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("session not found: " + sessionId));
    }

    private List<UUID> parse(String json) {
        try {
            return objectMapper.readValue(json, UUID_LIST);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("corrupt queue json", e);
        }
    }

    private String serialize(List<UUID> queue) {
        try {
            return objectMapper.writeValueAsString(queue);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("cannot serialize queue", e);
        }
    }
}
