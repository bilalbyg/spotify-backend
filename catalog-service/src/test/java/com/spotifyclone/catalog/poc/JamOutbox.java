package com.spotifyclone.catalog.poc;

import java.util.UUID;

/**
 * POC fixture — the seam under test for POC-4 (transactional outbox).
 *
 * <p>Invariant: once {@link #appendTrack} commits, its event is durably recorded
 * in the outbox and will be published by {@link #publishPending} — a publish
 * failure leaves the row unprocessed and it is retried, so no event is lost.
 *
 * <p>NOT production code.
 */
public interface JamOutbox {

    /** Creates a new, empty session. */
    UUID createSession();

    /** Appends a track and records its event atomically (same DB transaction). */
    UUID appendTrack(UUID sessionId, UUID trackId);

    /** Publishes all currently-unprocessed outbox rows. Returns the count published. */
    int publishPending();

    /** Number of outbox rows not yet published. */
    long unprocessedCount();

    /** Number of outbox rows already published. */
    long processedCount();
}
