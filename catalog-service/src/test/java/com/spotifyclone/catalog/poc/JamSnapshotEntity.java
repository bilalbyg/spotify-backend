package com.spotifyclone.catalog.poc;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

/**
 * POC fixture — a versioned snapshot for a session. {@code version} is the
 * monotonic snapshot version; a stale snapshot is detected by comparing against
 * it.
 *
 * <p>NOT production code.
 */
@Entity
@Table(name = "poc_jam_snapshot")
public class JamSnapshotEntity {

    @Id
    @Column(name = "session_id", nullable = false, updatable = false)
    private UUID sessionId;

    @Column(name = "version", nullable = false)
    private long version;

    @Column(name = "snapshot_value", nullable = false)
    private String value;

    protected JamSnapshotEntity() {
        // JPA
    }

    public JamSnapshotEntity(UUID sessionId, long version, String value) {
        this.sessionId = sessionId;
        this.version = version;
        this.value = value;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
