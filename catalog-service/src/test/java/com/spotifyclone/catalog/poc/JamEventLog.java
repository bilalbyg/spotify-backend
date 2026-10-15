package com.spotifyclone.catalog.poc;

import java.util.List;
import java.util.UUID;

/**
 * POC fixture — the seam under test for POC-5 (event log + reconnect replay).
 *
 * <p>Invariant: events carry a monotonic sequence number; a client that
 * reconnects with its last-seen sequence replays exactly the events it missed
 * ({@code seq > afterSeq}), in order — no lost, no duplicated, no out-of-order
 * events.
 *
 * <p>NOT production code.
 */
public interface JamEventLog {

    /** An immutable event with its monotonic sequence number. */
    record Event(long seq, String eventType, String payload) {
    }

    /** Appends an event and assigns the next monotonic sequence number. */
    void append(UUID sessionId, String eventType, String payload);

    /** Returns events with {@code seq > afterSeq}, in ascending order. */
    List<Event> replay(UUID sessionId, long afterSeq);
}
