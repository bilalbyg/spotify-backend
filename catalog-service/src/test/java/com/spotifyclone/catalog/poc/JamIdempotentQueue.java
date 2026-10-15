package com.spotifyclone.catalog.poc;

import java.util.List;
import java.util.UUID;

/**
 * POC fixture — the seam under test for POC-3 (idempotency key replay).
 *
 * <p>Invariant: a command identified by {@code (sessionId, idempotencyKey)} is
 * applied at most once. Retrying the same command (same key) must be a no-op —
 * it must not add a second track — and two concurrent retries of the same key
 * must result in exactly one application.
 *
 * <p>NOT production code. Throwaway educational fixture.
 */
public interface JamIdempotentQueue {

    /** Outcome of an append: whether it was applied, and the resulting queue. */
    record AppendResult(boolean applied, List<UUID> queue) {
    }

    /** Creates a new, empty session. */
    UUID createSession();

    /**
     * Appends {@code trackId} under {@code idempotencyKey}. If the key was
     * already used for this session, the append is a no-op and
     * {@code applied} is {@code false}.
     */
    AppendResult append(UUID sessionId, UUID trackId, UUID idempotencyKey);

    /** Returns the session's current queue. */
    List<UUID> queue(UUID sessionId);
}
