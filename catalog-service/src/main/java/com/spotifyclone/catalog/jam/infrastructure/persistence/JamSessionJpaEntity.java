package com.spotifyclone.catalog.jam.infrastructure.persistence;

import com.spotifyclone.catalog.jam.domain.model.SessionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.util.UUID;

/**
 * JPA persistence for the {@code JamSession} aggregate. The mutable collections
 * (queue, members, processed keys, skip votes) are serialized to JSON columns;
 * {@code @Version} gives optimistic locking (POC-1/2). The domain aggregate is
 * pure; this entity is its persistence shape.
 */
@Entity
@Table(name = "jam_session")
public class JamSessionJpaEntity {

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
    private SessionStatus status;

    @Column(name = "invite_link", nullable = false, unique = true)
    private UUID inviteLink;

    @Column(name = "queue_json", nullable = false, columnDefinition = "TEXT")
    private String queueJson;

    @Column(name = "members_json", nullable = false, columnDefinition = "TEXT")
    private String membersJson;

    @Column(name = "processed_keys_json", nullable = false, columnDefinition = "TEXT")
    private String processedKeysJson;

    @Column(name = "skip_votes_json", nullable = false, columnDefinition = "TEXT")
    private String skipVotesJson;

    protected JamSessionJpaEntity() {
        // JPA
    }

    public JamSessionJpaEntity(
            UUID id,
            UUID hostUserId,
            SessionStatus status,
            UUID inviteLink,
            String queueJson,
            String membersJson,
            String processedKeysJson,
            String skipVotesJson) {
        this.id = id;
        this.hostUserId = hostUserId;
        this.status = status;
        this.inviteLink = inviteLink;
        this.queueJson = queueJson;
        this.membersJson = membersJson;
        this.processedKeysJson = processedKeysJson;
        this.skipVotesJson = skipVotesJson;
    }

    public UUID getId() {
        return id;
    }

    public Long getVersion() {
        return version;
    }

    /** Used by the adapter to carry the optimistic-lock version across a save. */
    public void setVersion(Long version) {
        this.version = version;
    }

    public UUID getHostUserId() {
        return hostUserId;
    }

    public void setHostUserId(UUID hostUserId) {
        this.hostUserId = hostUserId;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    public UUID getInviteLink() {
        return inviteLink;
    }

    public String getQueueJson() {
        return queueJson;
    }

    public void setQueueJson(String queueJson) {
        this.queueJson = queueJson;
    }

    public String getMembersJson() {
        return membersJson;
    }

    public void setMembersJson(String membersJson) {
        this.membersJson = membersJson;
    }

    public String getProcessedKeysJson() {
        return processedKeysJson;
    }

    public void setProcessedKeysJson(String processedKeysJson) {
        this.processedKeysJson = processedKeysJson;
    }

    public String getSkipVotesJson() {
        return skipVotesJson;
    }

    public void setSkipVotesJson(String skipVotesJson) {
        this.skipVotesJson = skipVotesJson;
    }
}
