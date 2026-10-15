package com.spotifyclone.catalog.poc;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.util.UUID;

/**
 * POC fixture entity for a jam session. Stores its queue as a JSON array of
 * track UUIDs in a single {@code TEXT} column.
 *
 * <p><b>Slice 2 (green) adds the {@code @Version} column.</b> Hibernate
 * increments this on every {@code save} and emits
 * {@code UPDATE ... WHERE id = ? AND version = ?}; when a concurrent writer
 * committed first, the row count is zero and Hibernate raises an optimistic
 * lock exception. The service retries against the fresh row.
 *
 * <p><b>Slice 1 (red) had no {@code @Version}</b>, so concurrent
 * read-modify-write silently lost updates. The POC test reproduced that; this
 * entity turns it green. NOT production code.
 */
@Entity
@Table(name = "poc_jam_session")
public class JamSessionEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Version
    @Column(name = "version")
    private Long version;

    @Column(name = "queue_json", nullable = false, columnDefinition = "TEXT")
    private String queueJson;

    protected JamSessionEntity() {
        // JPA
    }

    public JamSessionEntity(UUID id, String queueJson) {
        this.id = id;
        this.queueJson = queueJson;
    }

    public UUID getId() {
        return id;
    }

    public Long getVersion() {
        return version;
    }

    public String getQueueJson() {
        return queueJson;
    }

    public void setQueueJson(String queueJson) {
        this.queueJson = queueJson;
    }
}
