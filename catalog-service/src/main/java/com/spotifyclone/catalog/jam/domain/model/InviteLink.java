package com.spotifyclone.catalog.jam.domain.model;

import java.util.UUID;

/**
 * An invite link (ADR-006): a UUID v4 that is unguessable by construction.
 * Participants join a session by presenting a matching link.
 */
public record InviteLink(UUID value) {
    public InviteLink {
        if (value == null) {
            throw new IllegalArgumentException("invite link must not be null");
        }
    }

    public static InviteLink generate() {
        return new InviteLink(UUID.randomUUID());
    }
}
