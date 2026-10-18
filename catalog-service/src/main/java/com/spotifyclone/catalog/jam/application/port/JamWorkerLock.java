package com.spotifyclone.catalog.jam.application.port;

import com.spotifyclone.catalog.jam.domain.model.JamId;

/**
 * Outbound port for distributed mutual exclusion (ADR-003). Ensures at most one
 * worker at a time runs {@code work} for a session across instances. A JVM-local
 * lock is insufficient; the Phase-5 Postgres advisory-lock adapter implements
 * this (POC-6).
 */
public interface JamWorkerLock {

    /**
     * Runs {@code work} while holding the session's distributed lock. Returns
     * {@code true} if the lock was acquired and the work ran, {@code false} if
     * another worker holds it.
     */
    boolean withLock(JamId sessionId, Runnable work);
}
