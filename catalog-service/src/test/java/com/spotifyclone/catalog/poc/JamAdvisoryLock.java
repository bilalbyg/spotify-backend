package com.spotifyclone.catalog.poc;

import java.util.UUID;

/**
 * POC fixture — the seam under test for POC-6 (distributed mutual exclusion).
 *
 * <p>Invariant: at most one worker at a time runs {@code work} for a given
 * session. A JVM-local lock ({@code synchronized}, {@code ReentrantLock}) only
 * serializes threads inside one JVM; across instances you need a database-level
 * lock (Postgres advisory lock, ADR-003).
 *
 * <p>NOT production code.
 */
public interface JamAdvisoryLock {

    /**
     * Runs {@code work} while holding the session's distributed lock. Returns
     * {@code true} if the lock was acquired and the work ran, {@code false} if
     * another worker holds the lock.
     */
    boolean withLock(UUID sessionId, Runnable work);
}
