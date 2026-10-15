package com.spotifyclone.catalog.poc;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;

import static com.spotifyclone.catalog.poc.JamSessionControl.Status.ACTIVE;
import static com.spotifyclone.catalog.poc.JamSessionControl.Status.ENDED;

/**
 * POC fixture — <b>Slice 2 optimistic-locking implementation</b> behind the
 * {@link JamSessionControl} seam.
 *
 * <p>Each operation runs in its own transaction and does read-modify-write on
 * the session aggregate, guarded by {@code @Version}. When a concurrent write
 * wins the version race, Hibernate raises
 * {@link ObjectOptimisticLockingFailureException}; we catch it, reload the
 * fresh row, and re-check the guard ({@code ACTIVE}) before retrying. This
 * stops the silent clobber Slice 1 demonstrated: an {@code end()} that reports
 * success is now guaranteed to persist.
 *
 * <p>NOT production code — throwaway fixture proving the claim before Phase 2-3
 * build the real Jam domain.
 */
@Service
public class JamSessionControlService implements JamSessionControl {

    private static final int MAX_RETRIES = 25;

    private final JamSessionControlRepository repository;
    private final TransactionTemplate transactionTemplate;

    public JamSessionControlService(
            JamSessionControlRepository repository,
            PlatformTransactionManager transactionManager) {
        this.repository = repository;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    public UUID createSession(UUID hostId) {
        return createSession(hostId, 5);
    }

    @Override
    public UUID createSession(UUID hostId, int activeMemberCount) {
        return transactionTemplate.execute(status -> {
            JamSessionControlEntity session =
                    new JamSessionControlEntity(UUID.randomUUID(), hostId, ACTIVE, activeMemberCount);
            return repository.save(session).getId();
        });
    }

    @Override
    public UUID transferHost(UUID sessionId, UUID newHostId) {
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            try {
                return transactionTemplate.execute(status -> transferHostOnce(sessionId, newHostId));
            } catch (ObjectOptimisticLockingFailureException e) {
                if (attempt == MAX_RETRIES - 1) {
                    throw e;
                }
            }
        }
        throw new IllegalStateException("unreachable");
    }

    @Override
    public void end(UUID sessionId) {
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            try {
                transactionTemplate.executeWithoutResult(status -> endOnce(sessionId));
                return;
            } catch (ObjectOptimisticLockingFailureException e) {
                if (attempt == MAX_RETRIES - 1) {
                    throw e;
                }
            }
        }
    }

    @Override
    public SessionState state(UUID sessionId) {
        return transactionTemplate.execute(status -> {
            JamSessionControlEntity session = require(sessionId);
            return new SessionState(session.getHostUserId(), session.getStatus());
        });
    }

    private UUID transferHostOnce(UUID sessionId, UUID newHostId) {
        JamSessionControlEntity session = require(sessionId);
        if (session.getStatus() != ACTIVE) {
            throw new IllegalStateException("session not active");
        }
        session.setHostUserId(newHostId);
        // @Version check fires here: UPDATE ... WHERE id=? AND version=?
        repository.save(session);
        return newHostId;
    }

    private void endOnce(UUID sessionId) {
        JamSessionControlEntity session = require(sessionId);
        if (session.getStatus() != ACTIVE) {
            throw new IllegalStateException("session not active");
        }
        session.setStatus(ENDED);
        repository.save(session);
    }

    private JamSessionControlEntity require(UUID sessionId) {
        return repository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("session not found: " + sessionId));
    }
}
