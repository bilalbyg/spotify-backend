package com.spotifyclone.catalog.poc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * POC fixture — <b>Slice 2 idempotent implementation</b> behind the
 * {@link JamIdempotentQueue} seam.
 *
 * <p>The idempotency key is reserved first in its own transaction via a
 * {@code (sessionId, idempotencyKey)} unique row. A duplicate insert raises
 * {@link DataIntegrityViolationException}, which we catch and turn into a
 * no-op. Only the caller that won the key reservation proceeds to append, so a
 * retry — sequential or concurrent — is applied at most once.
 *
 * <p><b>Slice 1 (red)</b> ignored the key and appended on every call, so a
 * retry grew the queue. This slice fixes that.
 *
 * <p><b>Known simplification:</b> reserve-then-apply uses two transactions, so
 * a crash between them would leave a reserved-but-unapplied key. A production
 * idempotency implementation needs a state machine (e.g. PENDING → APPLIED).
 * That nuance is out of scope for this POC. NOT production code.
 */
@Service
public class JamIdempotentQueueService implements JamIdempotentQueue {

    private static final TypeReference<List<UUID>> UUID_LIST = new TypeReference<>() {
    };

    private static final int MAX_RETRIES = 25;

    private final JamSessionRepository sessionRepository;
    private final JamIdempotencyRepository idempotencyRepository;
    private final ObjectMapper objectMapper;
    private final TransactionTemplate transactionTemplate;

    public JamIdempotentQueueService(
            JamSessionRepository sessionRepository,
            JamIdempotencyRepository idempotencyRepository,
            ObjectMapper objectMapper,
            PlatformTransactionManager transactionManager) {
        this.sessionRepository = sessionRepository;
        this.idempotencyRepository = idempotencyRepository;
        this.objectMapper = objectMapper;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    public UUID createSession() {
        return transactionTemplate.execute(status ->
                sessionRepository.save(new JamSessionEntity(UUID.randomUUID(), "[]")).getId());
    }

    @Override
    public AppendResult append(UUID sessionId, UUID trackId, UUID idempotencyKey) {
        // 1. Reserve the key. Duplicate insert => the command was already
        //    processed => no-op.
        try {
            reserve(sessionId, idempotencyKey, trackId);
        } catch (DataIntegrityViolationException e) {
            return new AppendResult(false, queue(sessionId));
        }

        // 2. Apply the append (own transaction, @Version + retry).
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            try {
                return transactionTemplate.execute(status -> {
                    JamSessionEntity session = require(sessionId);
                    List<UUID> queue = parse(session.getQueueJson());
                    queue.add(trackId);
                    session.setQueueJson(serialize(queue));
                    sessionRepository.save(session);
                    return new AppendResult(true, new ArrayList<>(queue));
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
    public List<UUID> queue(UUID sessionId) {
        return transactionTemplate.execute(status ->
                parse(require(sessionId).getQueueJson()));
    }

    private void reserve(UUID sessionId, UUID idempotencyKey, UUID trackId) {
        transactionTemplate.executeWithoutResult(status ->
                idempotencyRepository.saveAndFlush(
                        new JamIdempotencyEntity(sessionId, idempotencyKey, trackId)));
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
