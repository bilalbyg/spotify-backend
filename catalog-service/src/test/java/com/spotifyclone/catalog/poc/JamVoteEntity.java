package com.spotifyclone.catalog.poc;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * POC fixture entity for a single skip vote. The composite key
 * {@code (sessionId, trackId, userId)} is unique, so the same member cannot
 * vote twice on the same track — the database rejects the duplicate insert
 * (ADR-004 idempotency).
 *
 * <p>NOT production code.
 */
@Entity
@Table(name = "poc_jam_skip_vote")
@IdClass(JamVoteEntity.VoteKey.class)
public class JamVoteEntity {

    @Id
    @Column(name = "session_id", nullable = false)
    private UUID sessionId;

    @Id
    @Column(name = "track_id", nullable = false)
    private UUID trackId;

    @Id
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    protected JamVoteEntity() {
        // JPA
    }

    public JamVoteEntity(UUID sessionId, UUID trackId, UUID userId) {
        this.sessionId = sessionId;
        this.trackId = trackId;
        this.userId = userId;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public UUID getTrackId() {
        return trackId;
    }

    public UUID getUserId() {
        return userId;
    }

    public static class VoteKey implements Serializable {
        private UUID sessionId;
        private UUID trackId;
        private UUID userId;

        public VoteKey() {
        }

        public VoteKey(UUID sessionId, UUID trackId, UUID userId) {
            this.sessionId = sessionId;
            this.trackId = trackId;
            this.userId = userId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof VoteKey voteKey)) {
                return false;
            }
            return Objects.equals(sessionId, voteKey.sessionId)
                    && Objects.equals(trackId, voteKey.trackId)
                    && Objects.equals(userId, voteKey.userId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(sessionId, trackId, userId);
        }
    }
}
