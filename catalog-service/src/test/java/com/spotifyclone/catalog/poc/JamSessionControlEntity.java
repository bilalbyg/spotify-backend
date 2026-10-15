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
 * POC fixture entity for the session control aggregate (owner, lifecycle
 * status, and skip-vote state).
 *
 * <p>Holds the {@code @Version} column added in POC-2 (part 1) plus the
 * skip-vote fields: {@code activeMemberCount} drives the ADR-004 threshold and
 * {@code skippedTrackId} is the compare-and-swap marker that makes a skip
 * trigger exactly once under concurrent votes.
 *
 * <p>NOT production code.
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

    @Column(name = "active_member_count", nullable = false)
    private int activeMemberCount;

    @Column(name = "skipped_track_id")
    private UUID skippedTrackId;

    protected JamSessionControlEntity() {
        // JPA
    }

    public JamSessionControlEntity(
            UUID id,
            UUID hostUserId,
            JamSessionControl.Status status,
            int activeMemberCount) {
        this.id = id;
        this.hostUserId = hostUserId;
        this.status = status;
        this.activeMemberCount = activeMemberCount;
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

    public int getActiveMemberCount() {
        return activeMemberCount;
    }

    public UUID getSkippedTrackId() {
        return skippedTrackId;
    }

    public void setSkippedTrackId(UUID skippedTrackId) {
        this.skippedTrackId = skippedTrackId;
    }
}
