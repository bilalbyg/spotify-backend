package com.spotifyclone.catalog.poc;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.util.UUID;

/**
 * POC fixture — a playback session: the currently-playing track plus the queue.
 * Both fields are updated together by a skip, so the aggregate needs to be read
 * and written atomically.
 *
 * <p>NOT production code.
 */
@Entity
@Table(name = "poc_jam_playback")
public class JamPlaybackEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Version
    @Column(name = "version")
    private Long version;

    @Column(name = "current_track_id")
    private UUID currentTrackId;

    @Column(name = "queue_json", nullable = false, columnDefinition = "TEXT")
    private String queueJson;

    protected JamPlaybackEntity() {
        // JPA
    }

    public JamPlaybackEntity(UUID id, UUID currentTrackId, String queueJson) {
        this.id = id;
        this.currentTrackId = currentTrackId;
        this.queueJson = queueJson;
    }

    public UUID getId() {
        return id;
    }

    public UUID getCurrentTrackId() {
        return currentTrackId;
    }

    public void setCurrentTrackId(UUID currentTrackId) {
        this.currentTrackId = currentTrackId;
    }

    public String getQueueJson() {
        return queueJson;
    }

    public void setQueueJson(String queueJson) {
        this.queueJson = queueJson;
    }
}
