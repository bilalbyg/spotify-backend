package com.spotifyclone.catalog.poc;

import java.util.List;
import java.util.UUID;

/**
 * POC fixture — the seam under test for POC-7 (skip/add atomicity).
 *
 * <p>Invariant: {@code currentTrackId} always equals the head of {@code queue}
 * (or both are empty). A skip removes the head and advances
 * {@code currentTrackId} — it must be atomic so no observer ever sees a torn
 * state (queue advanced but current not, or vice versa).
 *
 * <p>NOT production code.
 */
public interface JamPlayback {

    record State(UUID currentTrackId, List<UUID> queue) {
    }

    /** Creates a session playing {@code firstTrack} with the given queue. */
    UUID createSession(UUID firstTrack, List<UUID> rest);

    /** Skips the current track: removes the head and advances current. */
    void skip(UUID sessionId);

    /** Appends a track to the queue. */
    void add(UUID sessionId, UUID trackId);

    /** Returns the current track and queue. */
    State observe(UUID sessionId);
}
