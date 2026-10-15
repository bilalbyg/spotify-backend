package com.spotifyclone.catalog.poc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * POC fixture — <b>Slice 2 optimistic-locking implementation</b> behind the
 * {@link JamQueue} seam.
 *
 * <p>Each {@code append} runs in its own transaction (via
 * {@link TransactionTemplate}) and does read-modify-write on the session
 * aggregate, now guarded by {@code @Version} on {@link JamSessionEntity}. When
 * a concurrent writer wins the version race, Hibernate raises
 * {@link ObjectOptimisticLockingFailureException}; we catch it, reload the
 * fresh row, and retry the append. This is the optimistic-lock + retry pattern
 * that prevents the silent lost update Slice 1 demonstrated.
 *
 * <p>NOT production code — a throwaway fixture to prove the claim before Phase
 * 2-3 builds the real Jam domain.
 */
@Service
public class JamQueueService implements JamQueue {

    private static final TypeReference<List<UUID>> UUID_LIST = new TypeReference<>() {
    };

    /** Ceiling on retries; a durable conflict beyond this surfaces as an error. */
    private static final int MAX_RETRIES = 25;

    private final JamSessionRepository repository;
    private final ObjectMapper objectMapper;
    private final TransactionTemplate transactionTemplate;

    public JamQueueService(
            JamSessionRepository repository,
            ObjectMapper objectMapper,
            PlatformTransactionManager transactionManager) {
        this.repository = repository;
        this.objectMapper = objectMapper;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    public UUID createSession() {
        return transactionTemplate.execute(status -> {
            JamSessionEntity session = new JamSessionEntity(UUID.randomUUID(), "[]");
            return repository.save(session).getId();
        });
    }

    @Override
    public List<UUID> append(UUID sessionId, UUID trackId) {
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            try {
                return transactionTemplate.execute(status -> appendOnce(sessionId, trackId));
            } catch (ObjectOptimisticLockingFailureException e) {
                // Another writer committed between our read and our write.
                // Reload the fresh aggregate and try again.
                if (attempt == MAX_RETRIES - 1) {
                    throw e;
                }
            }
        }
        throw new IllegalStateException("unreachable");
    }

    @Override
    public List<UUID> queue(UUID sessionId) {
        return transactionTemplate.execute(status -> {
            JamSessionEntity session = requireSession(sessionId);
            return parse(session.getQueueJson());
        });
    }

    private List<UUID> appendOnce(UUID sessionId, UUID trackId) {
        JamSessionEntity session = requireSession(sessionId);

        List<UUID> queue = parse(session.getQueueJson());
        queue.add(trackId);
        session.setQueueJson(serialize(queue));

        // @Version check fires here: UPDATE ... WHERE id=? AND version=?
        repository.save(session);
        return new ArrayList<>(queue);
    }

    private JamSessionEntity requireSession(UUID sessionId) {
        return repository.findById(sessionId)
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
