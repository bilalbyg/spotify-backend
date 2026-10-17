package com.spotifyclone.catalog.jam.domain.event;

import com.spotifyclone.catalog.jam.domain.model.InviteLink;
import com.spotifyclone.catalog.jam.domain.model.JamId;
import com.spotifyclone.catalog.jam.domain.model.TrackId;
import com.spotifyclone.catalog.jam.domain.model.UserId;

import java.util.List;

/**
 * Domain events emitted by the {@code JamSession} aggregate. Transport-agnostic
 * contracts — the application layer translates them to the outbox (POC-4) and
 * real-time notifications (POC-5).
 */
public sealed interface JamEvent {

    /** The session this event belongs to (drives realtime topic routing). */
    JamId sessionId();

    record SessionCreated(JamId sessionId, UserId host, InviteLink inviteLink) implements JamEvent {
    }

    record UserJoined(JamId sessionId, UserId user) implements JamEvent {
    }

    record TrackQueued(JamId sessionId, TrackId track, UserId actor) implements JamEvent {
    }

    record QueueReordered(JamId sessionId, List<TrackId> order, UserId actor) implements JamEvent {
    }

    record Skipped(JamId sessionId, TrackId skippedTrack) implements JamEvent {
    }

    record VoteSkipped(JamId sessionId, TrackId track, UserId voter) implements JamEvent {
    }

    record HostTransferred(JamId sessionId, UserId from, UserId to) implements JamEvent {
    }

    record SessionEnded(JamId sessionId, UserId actor) implements JamEvent {
    }
}
