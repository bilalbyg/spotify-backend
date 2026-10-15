package com.spotifyclone.catalog.poc;

import java.util.UUID;

/**
 * POC fixture — the seam under test for POC-2 (host transfer / session end
 * race).
 *
 * <p>The invariant POC-2 proves: when {@link #end} and {@link #transferHost}
 * race against the same session, the aggregate must settle into a coherent
 * state. In particular, once {@code end()} reports success, the session must
 * remain {@link Status#ENDED} — a concurrent host transfer must not silently
 * clobber it back to {@code ACTIVE} (the lost-update bug).
 *
 * <p>NOT production code. Throwaway educational fixture, mirroring the
 * {@code @Version}-on-aggregate strategy proven in POC-1.
 */
public interface JamSessionControl {

    enum Status {
        ACTIVE,
        ENDED
    }

    record SessionState(UUID hostId, Status status) {
    }

    /** Creates a new session owned by {@code hostId} with 5 active members. */
    UUID createSession(UUID hostId);

    /** Creates a new session owned by {@code hostId} with the given active member count. */
    UUID createSession(UUID hostId, int activeMemberCount);

    /**
     * Transfers host ownership to {@code newHostId}. Must reject the transfer
     * when the session is no longer active.
     */
    UUID transferHost(UUID sessionId, UUID newHostId);

    /** Ends the session. Must reject when the session is already ended. */
    void end(UUID sessionId);

    /** Returns the session's current owner and status. */
    SessionState state(UUID sessionId);
}
