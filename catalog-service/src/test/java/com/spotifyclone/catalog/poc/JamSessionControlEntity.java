package com.spotifyclone.catalog.poc;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.util.UUID;

/**
 * POC fixture entity for the session control aggregate (owner + lifecycle
 * status).
 *
 * <p><b>Slice 2 (green) adds the {@code @Version} column.</b> Hibernate emits
 * {@code UPDATE ... WHERE id=? AND version=?}, so a concurrent write that
 * committed first makes the loser raise an optimistic lock exception; the
 * service then re-reads the fresh row and re-checks its guard. This prevents
 * the silent clobber Slice 1 demonstrated (84/200 ends silently undone).
 *
 * <p><b>Slice 1 (red) had no {@code @Version}</b>. NOT production code.
 */
@Entity
@Table(name = "poc_jam_session_control")
public class JamSessionControlEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Version
    @Column(name = "version")
    private Long version;

    @Column(name = "host_user_id", nullable = false)
    private UUID hostUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private JamSessionControl.Status status;

    protected JamSessionControlEntity() {
        // JPA
    }

    public JamSessionControlEntity(UUID id, UUID hostUserId, JamSessionControl.Status status) {
        this.id = id;
        this.hostUserId = hostUserId;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public Long getVersion() {
        return version;
    }

    public UUID getHostUserId() {
        return hostUserId;
    }

    public void setHostUserId(UUID hostUserId) {
        this.hostUserId = hostUserId;
    }

    public JamSessionControl.Status getStatus() {
        return status;
    }

    public void setStatus(JamSessionControl.Status status) {
        this.status = status;
    }
}
