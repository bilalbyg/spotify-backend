package com.spotifyclone.catalog.poc;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * POC fixture entity recording that a command (idempotency key) has already
 * been processed for a session. The composite key
 * {@code (sessionId, idempotencyKey)} is unique, so a retry of the same command
 * is rejected by the database — the dedup that makes an append idempotent.
 *
 * <p>Implements {@link Persistable} and reports {@code isNew() == true} so
 * Spring Data always issues an INSERT (persist) rather than a merge. With a
 * pre-assigned composite id, the default {@code isNew()} would report false and
 * a duplicate would silently merge (SELECT + no-op) instead of raising the
 * unique-constraint violation that POC-3 relies on.
 *
 * <p>NOT production code.
 */
@Entity
@Table(name = "poc_jam_idempotency")
@IdClass(JamIdempotencyEntity.IdemKey.class)
public class JamIdempotencyEntity implements Persistable<JamIdempotencyEntity.IdemKey> {

    @Id
    @Column(name = "session_id", nullable = false)
    private UUID sessionId;

    @Id
    @Column(name = "idempotency_key", nullable = false)
    private UUID idempotencyKey;

    @Column(name = "track_id", nullable = false)
    private UUID trackId;

    protected JamIdempotencyEntity() {
        // JPA
    }

    public JamIdempotencyEntity(UUID sessionId, UUID idempotencyKey, UUID trackId) {
        this.sessionId = sessionId;
        this.idempotencyKey = idempotencyKey;
        this.trackId = trackId;
    }

    @Override
    public IdemKey getId() {
        return new IdemKey(sessionId, idempotencyKey);
    }

    @Override
    public boolean isNew() {
        // Always INSERT so a duplicate key raises a unique-constraint violation
        // (which the service turns into a no-op) instead of silently merging.
        return true;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public UUID getIdempotencyKey() {
        return idempotencyKey;
    }

    public UUID getTrackId() {
        return trackId;
    }

    public static class IdemKey implements Serializable {
        private UUID sessionId;
        private UUID idempotencyKey;

        public IdemKey() {
        }

        public IdemKey(UUID sessionId, UUID idempotencyKey) {
            this.sessionId = sessionId;
            this.idempotencyKey = idempotencyKey;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof IdemKey idemKey)) {
                return false;
            }
            return Objects.equals(sessionId, idemKey.sessionId)
                    && Objects.equals(idempotencyKey, idemKey.idempotencyKey);
        }

        @Override
        public int hashCode() {
            return Objects.hash(sessionId, idempotencyKey);
        }
    }
}
