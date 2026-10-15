package com.spotifyclone.catalog.poc;

import java.util.List;
import java.util.UUID;

/**
 * POC fixture — the seam under test for POC-1 (queue concurrent modification).
 *
 * <p>This interface is the public boundary the concurrency tests cross. It is
 * deliberately tiny so the test observes behaviour, not implementation: append
 * a track and observe the resulting queue; never reach into the entity or the
 * repository.
 *
 * <p>NOT production code. This is a throwaway educational fixture used to prove
 * the lost-update / optimistic-locking claims in
 * {@code docs/feasibility/spotify-jam/POC_PLAN.md} before the real Jam domain
 * is built in Phase 2-3.
 */
public interface JamQueue {

    /** Creates a new, empty jam session and returns its id. */
    UUID createSession();

    /**
     * Appends a track to the session's queue and returns the resulting ordered
     * queue. Must be safe under concurrent invocation: no append may be lost.
     */
    List<UUID> append(UUID sessionId, UUID trackId);

    /** Returns the session's current ordered queue. */
    List<UUID> queue(UUID sessionId);
}
